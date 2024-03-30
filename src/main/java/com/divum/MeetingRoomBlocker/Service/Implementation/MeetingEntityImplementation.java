package com.divum.MeetingRoomBlocker.Service.Implementation;

import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.CalenderEventDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityActivitiesDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.Enum.RoleEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.MeetingEntityService;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingEntityImplementation implements MeetingEntityService {

    private final MeetingEntityRepository meetingEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final RoomEntityRepository roomEntityRepository;

    @Override
    public ResponseEntity<?> viewslots(long id, LocalDate date) {
        try{
            Optional<RoomEntity> roomEntityOptional = roomEntityRepository.findById(id);
            if (roomEntityOptional.isEmpty() || roomEntityOptional.get().isDeleted()) {
                throw new DataNotFoundException(Constants.ROOM_NOT_FOUND);
            }
            if(date==null){ date=LocalDate.now(); }
            List<MeetingEntity> meetings = meetingEntityRepository.findByRoomEntityIdAndStartTimeBetween(id, date);
            List<MeetingEntityActivitiesDTO> meetingDTOs = meetings.stream()
                    .filter(meeting -> MeetingStatusEntity.ACCEPTED.equals(meeting.getStatus()))
                    .filter(meetingEntity -> meetingEntity.isDeleted()==false)
                    .map(this::convertToActivitiesDTO)
                    .collect(Collectors.toList());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .data(meetingDTOs)
                    .message(Constants.MEETING_FETCHED)
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
    public ResponseEntity<?> findByYear(int year) {
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> user = userEntityRepository.findByEmail(email);
            if(user.isEmpty()) {
                throw new DataNotFoundException(Constants.USER_NOT_FOUND);
            }
            UserEntity user1 = user.get();
            if (user1.getRole().equals(RoleEntity.EMPLOYEE)) {
                Long userId = user1.getId();
                List<MeetingEntity> meetings = meetingEntityRepository.findByYearByUser(userId, year);
                List<MeetingEntityDTO> meetingEntityDTOs = meetings.stream()
                        .filter(meeting -> MeetingStatusEntity.ACCEPTED.equals(meeting.getStatus()) || MeetingStatusEntity.COMPLETED.equals(meeting.getStatus()))
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
                List<CalenderEventDTO> calenderEvents = meetingEntityDTOs.stream()
                        .map(this::convertToCalenderEventDTO)
                        .collect(Collectors.toList());
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .message(Constants.MEETING_FETCHED)
                        .data(calenderEvents)
                        .build();
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            }
            else{
                List<MeetingEntity> meetings = meetingEntityRepository.findByYear(year);
                List<MeetingEntityDTO> meetingEntityDTOs = meetings.stream()
                        .filter(meeting -> MeetingStatusEntity.ACCEPTED.equals(meeting.getStatus()) || MeetingStatusEntity.COMPLETED.equals(meeting.getStatus()))
                        .filter(meetingEntity -> meetingEntity.isDeleted()==false)
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
                List<CalenderEventDTO> calenderEvents = meetingEntityDTOs.stream()
                        .map(this::convertToCalenderEventDTO)
                        .collect(Collectors.toList());
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .message(Constants.MEETING_FETCHED)
                        .data(calenderEvents)
                        .build();
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
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

    private CalenderEventDTO convertToCalenderEventDTO(MeetingEntityDTO meetingEntityDTO) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
        String start = dateFormat.format(meetingEntityDTO.getStartTime());
        String end = dateFormat.format(meetingEntityDTO.getEndTime());
        String hostName = getHostNameFromUserId(meetingEntityDTO.getHostId());
        String RoomName = getRoomNameFromRoomId(meetingEntityDTO.getRoomEntityId());
        return CalenderEventDTO.builder()
                .start(start)
                .end(end)
                .title(meetingEntityDTO.getMeetingName())
                .hostName(hostName)
                .roomName(RoomName)
                .build();
    }

    public String getRoomNameFromRoomId(Long userId) {
        Optional<RoomEntity> roomEntityOptional = roomEntityRepository.findById(userId);
        return roomEntityOptional.map(RoomEntity::getName).orElse(Constants.USER_NOT_FOUND);
    }

    public String getHostNameFromUserId(Long userId) {
        Optional<UserEntity> userEntityOptional = userEntityRepository.findUserNameById(userId);
        return userEntityOptional.map(UserEntity::getName).orElse(Constants.USER_NOT_FOUND);
    }

    private MeetingEntityDTO convertToDTO(MeetingEntity meetingEntity) {
        return MeetingEntityDTO.builder()
                .meetingName(meetingEntity.getMeetingName())
                .description(meetingEntity.getDescription())
                .startTime(meetingEntity.getStartTime())
                .endTime(meetingEntity.getEndTime())
                .status(meetingEntity.getStatus())
                .roomEntityId(meetingEntity.getRoomEntity().getId())
                .hostId(meetingEntity.getHost().getId())
                .build();
    }

    private MeetingEntityActivitiesDTO convertToActivitiesDTO(MeetingEntity meetingEntity) {
        LocalDateTime startTime = meetingEntity.getStartTime().toLocalDateTime();
        LocalDateTime endTime = meetingEntity.getEndTime().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return MeetingEntityActivitiesDTO.builder()
                .id(meetingEntity.getId())
                .meetingName(meetingEntity.getMeetingName())
                .description(meetingEntity.getDescription())
                .startDate(startTime.toLocalDate().toString())
                .endDate(endTime.toLocalDate().toString())
                .startTime(startTime.format(formatter))
                .endTime(endTime.format(formatter))
                .status(meetingEntity.getStatus())
                .roomName(meetingEntity.getRoomEntity().getName())
                .hostName(meetingEntity.getHost().getName())
                .hostEmail(meetingEntity.getHost().getEmail())
                .meetingCategory(meetingEntity.getMeetingCategoryEntity().getCategoryName())
                .build();
    }
}