package com.divum.MeetingRoomBlocker.API.AdminAPI;


import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingDeleteDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("${AdminMeeting_Api}")
public interface MeetingEntityAdminAPI {

    @PostMapping("${BlockMeeting-Admin}")
    public ResponseEntity<?> addMeetings(@RequestBody MeetingEntityRequestDTO meetingEntityDTO);

    @PutMapping("${UnBlockMeeting-Admin}")
    public ResponseEntity<?> deleteMeetings(@RequestBody MeetingDeleteDTO request);

    @GetMapping("${UpcomingMeetingHost-Admin}")
    public ResponseEntity<?> upcomingmeetings();

    @GetMapping("${Request-Admin}")
    public ResponseEntity<ResponseDTO> requests(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date);

    @GetMapping("${Unblock-Admin}")
    public ResponseEntity<?> unblock(@RequestParam Long roomId, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date);

    @GetMapping("${UpcomingMeeting-Admin}")
    public ResponseEntity<?> upcomingmeetingsbyDate();

    @GetMapping("${History-Admin}")
    public ResponseEntity<?> history(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate);

    @PutMapping("${Accept-Admin}")
    public ResponseEntity<?> acceptmeeting(@PathVariable Long id);

    @PutMapping("${Reject-Admin}")
    public ResponseEntity<?> rejectmeeting(@PathVariable Long id, @RequestBody MeetingEntityRequestDTO meetingEntityDTO);

}
