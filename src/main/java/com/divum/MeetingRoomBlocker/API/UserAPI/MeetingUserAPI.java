package com.divum.MeetingRoomBlocker.API.UserAPI;

import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDateTime;

@RequestMapping("${UserMeeting_Api}")
public interface MeetingUserAPI {

    @PostMapping("${BookMeeting-User}")
    public ResponseEntity<?> roomBooking(@RequestBody MeetingEntityRequestDTO meetingEntity);

   @PutMapping("${EditMeeting-User}")
    public ResponseEntity<?> editMeeting(@PathVariable Long id,@RequestBody MeetingEntityRequestDTO meetingEntityDTO);

   @GetMapping("${UserDashboard-User}")
    public ResponseEntity<?> getUserMeetingDate( @PathVariable Date date);

    @GetMapping("${CompletedMeeting-User}")
    public ResponseEntity<?> getCompletedMeeting( @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime date);

    @GetMapping("${UpcomingMeeting-User}")
    public ResponseEntity<?> getUpcomingMeeting(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                              LocalDateTime date, @PathVariable MeetingStatusEntity status);

    @PutMapping("${WithdrawMeeting-User}")
    public ResponseEntity<?> withdrawMeeting(@PathVariable Long id);


}

