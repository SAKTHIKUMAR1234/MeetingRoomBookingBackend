package com.divum.MeetingRoomBlocker.Service.Implementation.AdminImplementation;

import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.CreateEventDto;
import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.DeleteEventDto;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityActivitiesDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.UserDTO.InternalUserDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.Enum.RoleEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingCategoryEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Exception.InvalidDataException;
import com.divum.MeetingRoomBlocker.Exception.InvalidEmailException;
import com.divum.MeetingRoomBlocker.Repository.MeetingCategoryEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Scheduler.TaskTrigger;
import com.divum.MeetingRoomBlocker.Service.AdminService.MeetingEntityAdminService;
import com.divum.MeetingRoomBlocker.Service.CalendarService.CalendarEventService;
import com.divum.MeetingRoomBlocker.Service.MailServices.MailService;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class MeetingEntityAdminImplementation implements MeetingEntityAdminService {

    private final UserEntityRepository userEntityRepository;

    private final RoomEntityRepository roomEntityRepository;

    private final MeetingEntityRepository meetingEntityRepository;

    private final MeetingCategoryEntityRepository meetingCategoryEntityRepository;

    private final CalendarEventService calendarEventService;

    private final MailService mailService;

    private final TaskTrigger taskTrigger;

    @Override
    public ResponseEntity<?> addMeetings(MeetingEntityRequestDTO meetingEntityDTO) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            Optional<UserEntity> userEntityOptional = userEntityRepository.findByEmail(email);
            if (userEntityOptional.isEmpty()) {
                throw new DataNotFoundException(Constants.USER_NOT_FOUND);
            } else {
                UserEntity userEntity = userEntityOptional.get();
                Long id = userEntity.getId();
                LocalDateTime startTime = LocalDateTime.parse(meetingEntityDTO.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                LocalDateTime endTime = LocalDateTime.parse(meetingEntityDTO.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                Timestamp startTimestamp = Timestamp.valueOf(startTime);
                Timestamp endTimestamp = Timestamp.valueOf(endTime);
                int date = startTimestamp.compareTo(endTimestamp);
                if (date >= 0) {
                    ResponseDTO responseDTO = ResponseDTO.builder()
                            .httpStatus(HttpStatus.BAD_REQUEST.getReasonPhrase())
                            .message(Constants.TIME_ERROR)
                            .build();
                    return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
                }
                List<MeetingEntity> overlappingMeetings = meetingEntityRepository.findOverlappingMeetings(startTimestamp, endTimestamp, MeetingStatusEntity.ACCEPTED, meetingEntityDTO.getRoomEntityId());
                if (!overlappingMeetings.isEmpty()) {
                    ResponseDTO responseDTO1 = ResponseDTO.builder()
                            .httpStatus(HttpStatus.BAD_REQUEST.getReasonPhrase())
                            .message(Constants.MEETINGS_PRESENT)
                            .build();
                    return new ResponseEntity<>(responseDTO1, HttpStatus.BAD_REQUEST);
                }

                MeetingCategoryEntity clientMeetingCategory = meetingCategoryEntityRepository.findMeetingCategoryEntities("Client Meeting");
                MeetingEntity meetingEntity = MeetingEntity.builder().meetingName(meetingEntityDTO.getMeetingName()).description(meetingEntityDTO.getDescription()).startTime(startTimestamp).endTime(endTimestamp).meetingCategoryEntity(clientMeetingCategory).status(MeetingStatusEntity.ACCEPTED).isDeleted(false).build();

                RoomEntity roomEntity = roomEntityRepository.findById(meetingEntityDTO.getRoomEntityId()).orElseThrow(() -> new DataNotFoundException(Constants.ROOM_NOT_FOUND));
                meetingEntity.setRoomEntity(roomEntity);
                UserEntity host = userEntityRepository.findById(id).orElseThrow(() -> new DataNotFoundException(Constants.USER_NOT_FOUND));
                meetingEntity.setHost(host);

                MeetingEntity savedMeeting = meetingEntityRepository.save(meetingEntity);
                ResponseDTO responseDTO2 = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .data(savedMeeting)
                        .message(Constants.MEETING_BLOCKED)
                        .build();
                return new ResponseEntity<>(responseDTO2, HttpStatus.OK);
            }
        }
        catch (Exception e){
            ResponseDTO responseDTO2 = ResponseDTO.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO2, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> deleteMeetingById(List<Long> ids) {
        try {
            List<MeetingEntity> meetings = meetingEntityRepository.findByIdIn(ids);
            if (meetings.isEmpty()) {
                throw new DataNotFoundException(Constants.MEETING_NOT_FOUND);
            }
            meetings.forEach(meeting -> {
                meeting.setStatus(MeetingStatusEntity.CANCELLED);
                meeting.setDeleted(true);
            });
            meetingEntityRepository.saveAll(meetings);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .message(Constants.MEETING_UNBLOCKED)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> upcomingMeetingsbyhost() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userEntityRepository.findByEmail(email).orElseThrow(() -> new InvalidEmailException(email));

            List<MeetingEntity> hostMeetings = meetingEntityRepository.findByHostIdAndStartTimeAfterOrderByStartTime(user.getId(), Timestamp.valueOf(LocalDateTime.now()));

            List<MeetingEntity> attendeeMeetings = meetingEntityRepository.findByGuestListAndStartTimeAfterOrderByStartTime(user, Timestamp.valueOf(LocalDateTime.now()));

            List<MeetingEntity> allMeetings = Stream.concat(hostMeetings.stream(), attendeeMeetings.stream()).filter(meetingEntity -> MeetingStatusEntity.ACCEPTED.equals(meetingEntity.getStatus())).filter(meetingEntity -> !meetingEntity.isDeleted()).collect(Collectors.toList());

            List<MeetingEntityActivitiesDTO> upcomingMeetingsDTO = allMeetings.stream().map(this::convertToActivitiesDTO).collect(Collectors.toList());

            ResponseDTO responseDTO = ResponseDTO.builder().httpStatus(HttpStatus.OK.getReasonPhrase()).message(Constants.MEETING_FETCHED).data(upcomingMeetingsDTO).build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).message(Constants.INTERNAL_ERROR).build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseDTO requests(LocalDate date) {
        try {
            Timestamp timestamp;
            if (date == null) {
                LocalDateTime now = LocalDateTime.now();
                timestamp = Timestamp.valueOf(now);
            } else {
                timestamp = Timestamp.valueOf(date.atStartOfDay());
            }
            List<MeetingEntity> meetings = meetingEntityRepository.findByStartTimeAfterOrderByStartTime(timestamp);

            List<MeetingEntity> pendingMeetings = meetings.stream().filter(meeting -> MeetingStatusEntity.PENDING.equals(meeting.getStatus())).filter(meetingEntity -> meetingEntity.isDeleted() == false).collect(Collectors.toList());

            List<MeetingEntityActivitiesDTO> meetingDTOs = pendingMeetings.stream().map(this::convertToActivitiesDTO).collect(Collectors.toList());

            return ResponseDTO.builder().httpStatus(HttpStatus.OK.getReasonPhrase()).message(Constants.MEETING_FETCHED).data(meetingDTOs).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).message(Constants.INTERNAL_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<?> upcomingMeetingsbyDate() {
        Timestamp timestamp;
        LocalDateTime now = LocalDateTime.now();
        timestamp = Timestamp.valueOf(now);

        List<MeetingEntity> upcomingMeetings = meetingEntityRepository.findByStartTimeAfterOrderByStartTime(timestamp);
            List<MeetingEntityActivitiesDTO> upcomingMeetingsDTO = upcomingMeetings.stream()
                    .filter(meeting -> meeting.getHost().getRole().equals(RoleEntity.EMPLOYEE))
                    .filter(meeting -> MeetingStatusEntity.ACCEPTED.equals(meeting.getStatus()))
                    .filter(meetingEntity -> meetingEntity.isDeleted()==false)
                    .map(this::convertToActivitiesDTO)
                    .collect(Collectors.toList());

        ResponseDTO responseDTO = ResponseDTO.builder().httpStatus(HttpStatus.OK.getReasonPhrase()).message(Constants.MEETING_FETCHED).data(upcomingMeetingsDTO).build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> history(LocalDate startDate, LocalDate endDate) {
        try {
            Timestamp startTimestamp;
            Timestamp endTimestamp;

            if (startDate == null && endDate == null) {
                LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

                startTimestamp = Timestamp.valueOf(startOfMonth);
                endTimestamp = Timestamp.valueOf(endOfMonth);
            } else {
                startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
                endTimestamp = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusNanos(1));
            }

            List<MeetingEntity> meetings = meetingEntityRepository.findByStartDateAndEndDate(startTimestamp, endTimestamp);

            List<MeetingEntityActivitiesDTO> meetingsDTO = meetings.stream().filter(meeting -> MeetingStatusEntity.COMPLETED.equals(meeting.getStatus()) || MeetingStatusEntity.REJECTED.equals(meeting.getStatus())).map(this::convertToActivitiesDTO).collect(Collectors.toList());

            Map<String, Long> categoryCounts = meetings.stream().filter(meeting -> MeetingStatusEntity.COMPLETED.equals(meeting.getStatus()) || MeetingStatusEntity.REJECTED.equals(meeting.getStatus()) || MeetingStatusEntity.ACCEPTED.equals(meeting.getStatus())).collect(Collectors.groupingBy(meeting -> meeting.getMeetingCategoryEntity().getCategoryName(), Collectors.counting()));

            categoryCounts.putIfAbsent("Manager Meeting", 0L);
            categoryCounts.putIfAbsent("Team Meeting", 0L);
            categoryCounts.putIfAbsent("Client Meeting", 0L);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("meetings", meetingsDTO);
            responseData.put("categoryCounts", categoryCounts);

            ResponseDTO responseDTO = ResponseDTO.builder().httpStatus(HttpStatus.OK.getReasonPhrase()).message(Constants.MEETING_FETCHED).data(responseData).build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseDTO responseDTO = ResponseDTO.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).message(Constants.INTERNAL_ERROR).build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> unblock(Long roomId, LocalDate date) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Long id = userEntityRepository.findByEmail(email).orElseThrow(() -> new InvalidEmailException(email)).getId();
            if (!roomEntityRepository.existsById(roomId) || !userEntityRepository.existsById(id)) {
                throw new DataNotFoundException(Constants.DATA_NOT_FOUND);
            }
            LocalDateTime localDateTime;
            if(date==null){
                localDateTime = LocalDateTime.now();
            }
            else{
                localDateTime = date.atStartOfDay();
            }
            List<MeetingEntity> meetings = meetingEntityRepository.findByRoomEntityIdAndHostIdAndStartTimeBetween(
                    roomId,
                    id,
                    localDateTime
            );
            List<MeetingEntityActivitiesDTO> meetingDTOs = meetings.stream()
                    .filter(meeting -> MeetingStatusEntity.ACCEPTED.equals(meeting.getStatus()))
                    .filter(meetingEntity -> meetingEntity.isDeleted()==false)
                    .map(this::convertToActivitiesDTO)
                    .collect(Collectors.toList());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .message(Constants.MEETING_FETCHED)
                    .data(meetingDTOs)
                    .build();
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }
        catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> acceptMeeting(Long meetingId) {
        try {
            MeetingEntity meeting = meetingEntityRepository.findById(meetingId).orElseThrow(() -> new DataNotFoundException(Constants.MEETING_NOT_FOUND));

            Timestamp StartTime = meeting.getStartTime();
            Timestamp EndTime = meeting.getEndTime();
            List<MeetingEntity> overlappingMeetings = meetingEntityRepository.findOverlappingMeetings(StartTime, EndTime, MeetingStatusEntity.ACCEPTED, meeting.getRoomEntity().getId());

            if (!overlappingMeetings.isEmpty()) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(Constants.MEETINGS_PRESENT)
                        .build();
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            meeting.setStatus(MeetingStatusEntity.ACCEPTED);
            meetingEntityRepository.save(meeting);

            mailService.SendMailToMeetingAttenders(meetingId);

            taskTrigger.scheduleTasksAtSpecifiedTimes(meeting);

            CreateEventDto createEventDto = convertMeetingToCreateEventDto(meeting);
            calendarEventService.calendarCreateEvent(createEventDto);

                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .message(Constants.CALENDER_CREATED)
                        .build();
                return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }   catch (Exception e){
            e.printStackTrace();
            ResponseDTO responseDTO = ResponseDTO.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).message(Constants.INTERNAL_ERROR).build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> rejectMeeting(Long meetingId, MeetingEntityRequestDTO meetingEntityDTO) {        try {
        MeetingEntity meeting = meetingEntityRepository.findById(meetingId)
                .orElseThrow(() -> new DataNotFoundException(Constants.MEETING_NOT_FOUND));
        if (meeting.getStatus().equals(MeetingStatusEntity.PENDING) && !meeting.isDeleted()) {
            meeting.setStatus(MeetingStatusEntity.REJECTED);
            meeting.setReason(meetingEntityDTO.getReason());
            meetingEntityRepository.save(meeting);
            String reason = meeting.getReason();
            mailService.MeetingCancel(meetingId, reason);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .message(Constants.MEETING_REJECTED)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
        else if (MeetingStatusEntity.ACCEPTED.equals(meeting.getStatus()) && meeting.isDeleted() == false) {
            meeting.setReason("Unavoidable");
            meeting.setStatus(MeetingStatusEntity.REJECTED);
            meetingEntityRepository.save(meeting);
            mailService.SendMailToMeetingAttenders(meetingId);
            deleteEventFromCalendar(meeting);
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .message(Constants.CALENDER_DELETED)
                        .build();
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ResponseDTO responseDTO = ResponseDTO.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).message(Constants.INTERNAL_ERROR).build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<?> deleteEventFromCalendar(MeetingEntity meeting) {
        try {
            String eventId = meeting.getOriginalMeetingId();
            String organizerEmail = meeting.getHost().getEmail();
            DeleteEventDto deleteEventDto = new DeleteEventDto(organizerEmail);
            return calendarEventService.calendarDeleteEvent(eventId, deleteEventDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.CALENDER_ERROR);
        }
    }

    public MeetingEntityActivitiesDTO convertToActivitiesDTO(MeetingEntity meetingEntity) {
        LocalDateTime startTime = meetingEntity.getStartTime().toLocalDateTime();
        LocalDateTime endTime = meetingEntity.getEndTime().toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return MeetingEntityActivitiesDTO.builder().id(meetingEntity.getId()).meetingName(meetingEntity.getMeetingName()).description(meetingEntity.getDescription()).startDate(startTime.toLocalDate().format(dateFormatter)).endDate(endTime.toLocalDate().format(dateFormatter)).startTime(startTime.format(formatter)).endTime(endTime.format(formatter)).status(meetingEntity.getStatus()).roomName(meetingEntity.getRoomEntity().getName()).hostName(meetingEntity.getHost().getName()).hostEmail(meetingEntity.getHost().getEmail()).meetingCategory(meetingEntity.getMeetingCategoryEntity().getCategoryName()).guestList(meetingEntity.getGuestList().stream().map(this::covertToUserList).collect(Collectors.toList())).build();
    }

    public CreateEventDto convertMeetingToCreateEventDto(MeetingEntity meeting) {
        CreateEventDto createEventDto = new CreateEventDto();
        createEventDto.setMeetingId(meeting.getId());
        createEventDto.setSummary(meeting.getMeetingName());

        createEventDto.setStartTime(new Date(meeting.getStartTime().getTime()));
        createEventDto.setEndTime(new Date(meeting.getEndTime().getTime()));

        createEventDto.setDescription(meeting.getDescription());

        List<String> attendees = meeting.getGuestList().stream().map(UserEntity::getEmail).collect(Collectors.toList());
        createEventDto.setAttendees(attendees);

        createEventDto.setOrganizerEmail(meeting.getHost().getEmail());

        return createEventDto;
    }

    public InternalUserDTO covertToUserList(UserEntity userEntity) {
        return InternalUserDTO.builder().email(userEntity.getEmail()).name(userEntity.getName()).build();
    }


}