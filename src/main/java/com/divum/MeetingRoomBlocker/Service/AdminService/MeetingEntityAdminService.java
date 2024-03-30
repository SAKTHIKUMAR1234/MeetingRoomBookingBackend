package com.divum.MeetingRoomBlocker.Service.AdminService;

import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface MeetingEntityAdminService {

    ResponseEntity<?> addMeetings(MeetingEntityRequestDTO meetingEntityDTO);

    ResponseEntity<?> deleteMeetingById(List<Long> ids);

    ResponseEntity<?> upcomingMeetingsbyhost();

    ResponseDTO requests(LocalDate date);

    ResponseEntity<?> upcomingMeetingsbyDate();

    ResponseEntity<?> history(LocalDate startDate, LocalDate endDate);

    ResponseEntity<?> unblock(Long roomId, LocalDate date);

    ResponseEntity<?> acceptMeeting(Long meetingId);

    ResponseEntity<?> rejectMeeting(Long meetingId, MeetingEntityRequestDTO meetingEntityDTO);
}
