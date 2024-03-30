package com.divum.MeetingRoomBlocker.Controller.AdminController;


import com.divum.MeetingRoomBlocker.API.AdminAPI.MeetingEntityAdminAPI;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingDeleteDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Service.AdminService.MeetingEntityAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class MeetingEntityAdminController implements MeetingEntityAdminAPI {

    private final MeetingEntityAdminService meetingEntityAdminService;

    @Override
    public ResponseEntity<?> addMeetings(@RequestBody MeetingEntityRequestDTO meetingEntityDTO){
        return meetingEntityAdminService.addMeetings(meetingEntityDTO);
    }

    @Override
    public ResponseEntity<?> deleteMeetings(@RequestBody MeetingDeleteDTO request){
        List<Long> ids = request.getIds();
        return meetingEntityAdminService.deleteMeetingById(ids);
    }

    @Override
    public ResponseEntity<?> upcomingmeetings(){
        return meetingEntityAdminService.upcomingMeetingsbyhost();
    }

    @Override
    public ResponseEntity<ResponseDTO> requests(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        ResponseDTO responseDTO = meetingEntityAdminService.requests(date);
        return ResponseEntity.ok(responseDTO);
    }

    @Override
    public ResponseEntity<?> unblock(@RequestParam Long roomId, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return meetingEntityAdminService.unblock(roomId, date);
    }

    @Override
    public ResponseEntity<?> upcomingmeetingsbyDate(){
        return meetingEntityAdminService.upcomingMeetingsbyDate();
    }

    @Override
    public ResponseEntity<?> history(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return meetingEntityAdminService.history(startDate, endDate);
    }

    @Override
    public ResponseEntity<?> acceptmeeting(@PathVariable Long id){
        return meetingEntityAdminService.acceptMeeting(id);
    }

    @Override
    public ResponseEntity<?> rejectmeeting(@PathVariable Long id, @RequestBody(required = false) MeetingEntityRequestDTO meetingEntityDTO){
        return meetingEntityAdminService.rejectMeeting(id, meetingEntityDTO);
    }

}
