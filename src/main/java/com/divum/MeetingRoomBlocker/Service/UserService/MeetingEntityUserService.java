package com.divum.MeetingRoomBlocker.Service.UserService;

import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;

@Service
public interface MeetingEntityUserService {

    ResponseEntity<?> addMeeting(MeetingEntityRequestDTO meetingEntity);

    ResponseEntity<?> editMeetingDetails(Long id, MeetingEntityRequestDTO meetingEntityDTO);

    ResponseEntity<?> getUserMeetingDetails( Date date);

    ResponseEntity<?> getUpcomingMeetingDetails(LocalDateTime date, MeetingStatusEntity status);

    ResponseEntity<?> getCompletedMeeting( LocalDateTime date);

    ResponseEntity<?> withdrawMeeting(Long id);


}
