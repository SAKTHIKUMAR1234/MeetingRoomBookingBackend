package com.divum.MeetingRoomBlocker.Service.Implementation.UserImplementation;

import com.divum.MeetingRoomBlocker.DTO.*;
import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.DeleteEventDto;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingActivityCompletedDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityActivitiesDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.UserDTO.InternalUserDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Exception.InvalidDataException;
import com.divum.MeetingRoomBlocker.Exception.InvalidEmailException;
import com.divum.MeetingRoomBlocker.Repository.*;
import com.divum.MeetingRoomBlocker.Service.CalendarService.CalendarEventService;
import com.divum.MeetingRoomBlocker.Service.MailServices.MailService;
import com.divum.MeetingRoomBlocker.Service.UserService.MeetingEntityUserService;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Builder
@RequiredArgsConstructor
public class MeetingEntityUserImplementation implements MeetingEntityUserService {

    private final MeetingCategoryEntityRepository meetingCategoryEntityRepository;
    private final MeetingEntityRepository meetingEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final RoomEntityRepository roomEntityRepository;
    private final CalendarEventService calendarEventService;
    private final MailService mailService;
    private final FeedbackEntityRepository feedbackEntityRepository;

    @Override
    public ResponseEntity<?> addMeeting(MeetingEntityRequestDTO meetingEntity) {
        try {
            List<UserEntity> internalAttendees=userEntityRepository.findByEmailIn(meetingEntity.getUserEntityList());
            if(internalAttendees.isEmpty()){
                throw new InvalidEmailException(Constants.USER_NOT_FOUND);
            }
            LocalDateTime startTime = LocalDateTime.parse(meetingEntity.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            LocalDateTime endTime = LocalDateTime.parse(meetingEntity.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            Timestamp startTimestamp = Timestamp.valueOf(startTime);
            Timestamp endTimestamp = Timestamp.valueOf(endTime);
            int date=startTimestamp.compareTo(endTimestamp);
            if(date>=0){
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(Constants.TIME_ERROR)
                        .build();
                return new ResponseEntity<>(responseDTO,HttpStatus.BAD_REQUEST);
            }
            long durationDiff = endTimestamp.getTime() - startTimestamp.getTime();
            if (durationDiff < 30 * 60 * 1000) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(Constants.MEETING_DURATION_ERROR)
                        .build();
                return new ResponseEntity<>(responseDTO,HttpStatus.BAD_REQUEST);
            }
            String email=SecurityContextHolder.getContext().getAuthentication().getName();
            var newMeeting = MeetingEntity.builder()
                    .meetingName(meetingEntity.getMeetingName())
                    .startTime(startTimestamp)
                    .endTime(endTimestamp)
                    .meetingCategoryEntity(meetingCategoryEntityRepository.findMeetingCategoryEntities(meetingEntity.getMeetingCategory()))
                    .description(meetingEntity.getDescription())
                    .guestList(internalAttendees)
                    .host(userEntityRepository.findByEmail(email).orElseThrow(()-> new InvalidEmailException(Constants.USER_NOT_FOUND) ))
                    .roomEntity(roomEntityRepository.findById(meetingEntity.getRoomEntityId()).orElseThrow(()->new DataNotFoundException(Constants.ROOM_NOT_FOUND)))
                    .status(MeetingStatusEntity.PENDING)
                    .build();
            meetingEntityRepository.save(newMeeting);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .data(Constants.MEETING_ADDED)
                    .message(Constants.MEETING_ADDED)
                    .build();
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }
        catch (Exception e){
        ResponseDTO responseDTO = ResponseDTO.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(Constants.INTERNAL_ERROR)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }

    @Override
    public ResponseEntity<?> editMeetingDetails(Long id, MeetingEntityRequestDTO meetingEntity) {
        try {
            List<UserEntity> internalAttendees=userEntityRepository.findByEmailIn(meetingEntity.getUserEntityList());
            if(internalAttendees.isEmpty()){
                throw new InvalidEmailException(Constants.USER_NOT_FOUND);
            }
            String email=SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity hostDetails=userEntityRepository.findByEmail(email).orElseThrow(()->new InvalidEmailException(Constants.USER_NOT_FOUND));
            MeetingEntity meeting = meetingEntityRepository.findById(id).orElseThrow(() -> new DataNotFoundException(Constants.MEETING_NOT_FOUND));
            LocalDateTime startTime = LocalDateTime.parse(meetingEntity.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            LocalDateTime endTime = LocalDateTime.parse(meetingEntity.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            Timestamp startTimestamp = Timestamp.valueOf(startTime);
            Timestamp endTimestamp = Timestamp.valueOf(endTime);
            int date=startTimestamp.compareTo(endTimestamp);
            if(date>=0){
                throw new InvalidDataException(Constants.TIME_ERROR);
            }
            if (meeting.getStatus().equals(MeetingStatusEntity.PENDING) && meeting.getHost().getEmail().equals(email)) {
                meeting = MeetingEntity.builder()
                        .id(meeting.getId())
                        .meetingName(meetingEntity.getMeetingName())
                        .startTime(startTimestamp)
                        .endTime(endTimestamp)
                        .meetingCategoryEntity(meetingCategoryEntityRepository.findMeetingCategoryEntities(meetingEntity.getMeetingCategory()))
                        .description(meetingEntity.getDescription())
                        .guestList(internalAttendees)
                        .createdAt(meeting.getCreatedAt())
                        .host(hostDetails)
                        .originalMeetingId(meeting.getOriginalMeetingId())
                        .roomEntity(roomEntityRepository.findById(meetingEntity.getRoomEntityId()).orElseThrow(()->new DataNotFoundException("Room not found")))
                        .status(MeetingStatusEntity.PENDING)
                        .build();
                meetingEntityRepository.save(meeting);
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .data(Constants.MEETING_ADDED)
                        .message(Constants.MEETING_ADDED)
                        .build();
                return new ResponseEntity<>(responseDTO,HttpStatus.OK);
            }
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .data(Constants.EDIT_PERMISSION)
                    .message(Constants.EDIT_PERMISSION)
                    .build();
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }
        catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getUserMeetingDetails( Date date) {
        try {
            String email= SecurityContextHolder.getContext().getAuthentication().getName();
            Long userId = userEntityRepository.findByEmail(email).orElseThrow(() -> new InvalidEmailException(email)).getId();
            List<MeetingEntity> meetingDetails = meetingEntityRepository.findMeetingsByUserEmailAndDate(date,MeetingStatusEntity.ACCEPTED,MeetingStatusEntity.COMPLETED, userId);
            List<MeetingEntityResponseDTO> meetingDTOs = meetingDetails.stream()
                    .map(this::convertToUserDTO)
                    .collect(Collectors.toList());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .message(Constants.MEETING_FETCHED)
                    .data(meetingDTOs)
                    .build();
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }
        catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<?> getUpcomingMeetingDetails( LocalDateTime date, MeetingStatusEntity status) {
        try {
            if(date==null){
                date=LocalDateTime.now();
            }
            String email= SecurityContextHolder.getContext().getAuthentication().getName();
            Long userId = userEntityRepository.findByEmail(email).orElseThrow(() -> new InvalidEmailException(email)).getId();
            if(!(status.equals(MeetingStatusEntity.ACCEPTED) || status.equals(MeetingStatusEntity.PENDING))){
               throw  new InvalidDataException(Constants.STATUS_INVALID) ;
            }
            if(status.equals(MeetingStatusEntity.ACCEPTED)){
            List<MeetingEntity> upcomingMeeting = meetingEntityRepository.findByHostIdAndDateAndUpcomingMeeting(date, status, userId);
            List<MeetingEntityActivitiesDTO> upcomingMeetingList = upcomingMeeting.stream()
                    .map(this::convertToActivitiesDTO)
                    .collect(Collectors.toList());
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .message(Constants.MEETING_FETCHED)
                        .data(upcomingMeetingList)
                        .build();
                return new ResponseEntity<>(responseDTO,HttpStatus.OK);}
            else {
                List<MeetingEntity> upcomingMeeting = meetingEntityRepository.findByHostIdAndPendingMeeting(date, status, userId);

                List<MeetingEntityActivitiesDTO> upcomingMeetingList = upcomingMeeting.stream()
                        .map(this::convertToActivitiesDTO)
                        .collect(Collectors.toList());
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .message(Constants.MEETING_FETCHED)
                        .data(upcomingMeetingList)
                        .build();
                return new ResponseEntity<>(responseDTO,HttpStatus.OK);
            }
        }
        catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }

    @Override
    public ResponseEntity<?> getCompletedMeeting( LocalDateTime date) {
        try{
            if(date==null){
                date=LocalDateTime.now();

            }
            String email= SecurityContextHolder.getContext().getAuthentication().getName();
            Long userId = userEntityRepository.findByEmail(email).orElseThrow(() -> new InvalidEmailException(email)).getId();
            List<MeetingEntity> completedMeeting = meetingEntityRepository.findByHostIdAndDateAndCompletedMeeting(date,MeetingStatusEntity.COMPLETED,MeetingStatusEntity.REJECTED, userId);
            List<MeetingActivityCompletedDTO> upcomingMeetingList = completedMeeting.stream()
                    .map(this::convertToCompletedActivitiesDTO)
                    .collect(Collectors.toList());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .message(Constants.MEETING_FETCHED)
                    .data(upcomingMeetingList)
                    .build();
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }
        catch (Exception e){
        ResponseDTO responseDTO = ResponseDTO.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(Constants.INTERNAL_ERROR)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<?> withdrawMeeting(Long id) {
        try{
            MeetingEntity meetingEntity=meetingEntityRepository.findById(id).orElseThrow(()->new DataNotFoundException(Constants.USER_NOT_FOUND));
            String email=SecurityContextHolder.getContext().getAuthentication().getName();
            if(email.equals(meetingEntity.getHost().getEmail())) {
                MeetingStatusEntity updatedStatus=meetingEntity.getStatus().equals(MeetingStatusEntity.PENDING)?MeetingStatusEntity.PENDING:MeetingStatusEntity.CANCELLED;
               meetingEntity.setStatus(updatedStatus);
               meetingEntity.setDeleted(true);
                meetingEntityRepository.save(meetingEntity);
                if(updatedStatus.equals(MeetingStatusEntity.CANCELLED)){
                    mailService.SendMailToMeetingAttenders(id);
                    ResponseEntity<?> calendarResponse = deleteEventFromCalendar(meetingEntity);
                    if (!calendarResponse.getStatusCode().is2xxSuccessful()) {

                        ResponseDTO responseDTO = ResponseDTO.builder()
                                .httpStatus(HttpStatus.OK.getReasonPhrase())
                                .message(Constants.CALENDER_ERROR)
                                .build();
                        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
                    }
                }
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .message(Constants.MEETING_DELETED)
                        .build();
                return new ResponseEntity<>(responseDTO,HttpStatus.OK);
            }
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> deleteEventFromCalendar(MeetingEntity meeting) {
        try {
            String eventId = meeting.getOriginalMeetingId();
            String organizerEmail = meeting.getHost().getEmail();
            DeleteEventDto deleteEventDto = new DeleteEventDto(organizerEmail);
            return calendarEventService.calendarDeleteEvent(eventId, deleteEventDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.CALENDER_ERROR);
        }
    }

    private MeetingEntityResponseDTO convertToUserDTO(MeetingEntity meetingEntity) {
        LocalDateTime startTime = meetingEntity.getStartTime().toLocalDateTime();
        LocalDateTime endTime = meetingEntity.getEndTime().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return MeetingEntityResponseDTO.builder()
                .meetingName(meetingEntity.getMeetingName())
                .startDate(startTime.toLocalDate().format(dateFormatter))
                .endDate(endTime.toLocalDate().format(dateFormatter))
                .startTime(startTime.toLocalTime().format(formatter))
                .endTime(endTime.toLocalTime().format(formatter))
                .roomName(meetingEntity.getRoomEntity().getName())
                .hostName(meetingEntity.getHost().getName())
                .meetingCategory(meetingEntity.getMeetingCategoryEntity().getCategoryName())
                .build();
    }

    private MeetingEntityActivitiesDTO convertToActivitiesDTO(MeetingEntity meetingEntity) {
        LocalDateTime startTime = meetingEntity.getStartTime().toLocalDateTime();
        LocalDateTime endTime = meetingEntity.getEndTime().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return MeetingEntityActivitiesDTO.builder()
                .id(meetingEntity.getId())
                .meetingName(meetingEntity.getMeetingName())
                .description(meetingEntity.getDescription())
                .startDate(startTime.toLocalDate().format(dateFormatter))
                .endDate(endTime.toLocalDate().format(dateFormatter))
                .startTime(startTime.toLocalTime().format(formatter))
                .endTime(endTime.toLocalTime().format(formatter))
                .description(meetingEntity.getDescription())
                .status(meetingEntity.getStatus())
                .roomName(meetingEntity.getRoomEntity().getName())
                .roomEntityId(meetingEntity.getRoomEntity().getId())
                .hostName(meetingEntity.getHost().getName())
                .hostEmail(meetingEntity.getHost().getEmail())
                .meetingCategory(meetingEntity.getMeetingCategoryEntity().getCategoryName())
                .guestList(meetingEntity.getGuestList() .stream()
                        .map(this::covertToUserList)
                        .collect(Collectors.toList()))
                .build();
    }

    private MeetingActivityCompletedDTO convertToCompletedActivitiesDTO(MeetingEntity meetingEntity) {
        LocalDateTime startTime = meetingEntity.getStartTime().toLocalDateTime();
        LocalDateTime endTime = meetingEntity.getEndTime().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return MeetingActivityCompletedDTO.builder()
                .id(meetingEntity.getId())
                .meetingName(meetingEntity.getMeetingName())
                .startDate(startTime.toLocalDate().format(dateFormatter))
                .endDate(endTime.toLocalDate().format(dateFormatter))
                .startTime(startTime.toLocalTime().format(formatter))
                .endTime(endTime.toLocalTime().format(formatter))
                .roomName(meetingEntity.getRoomEntity().getName())
                .hostName(meetingEntity.getHost().getName())
                .hostEmail(meetingEntity.getHost().getEmail())
                .status(meetingEntity.getStatus())
                .hostEmail(meetingEntity.getHost().getEmail())
                .description(meetingEntity.getDescription())
                .reason(meetingEntity.getReason())
                .feedback(feedbackEntityRepository.findByMeetingEntity(meetingEntity).isPresent())
                .meetingCategory(meetingEntity.getMeetingCategoryEntity().getCategoryName())
                .guestList(meetingEntity.getGuestList() .stream()
                        .map(this::covertToUserList)
                        .collect(Collectors.toList()))
                .build();
    }

    private InternalUserDTO covertToUserList(UserEntity userEntity) {
        return InternalUserDTO.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .build();

    }
}
