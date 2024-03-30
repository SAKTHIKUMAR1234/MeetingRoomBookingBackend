package com.divum.MeetingRoomBlocker.Controller.UserController;

import com.divum.MeetingRoomBlocker.API.UserAPI.MeetingUserAPI;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Service.UserService.MeetingEntityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDateTime;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class MeetingUserController implements MeetingUserAPI {

    private final MeetingEntityUserService meetingEntityUserService;

    @Override
    public ResponseEntity<?> roomBooking(@RequestBody MeetingEntityRequestDTO meetingEntity){
        return meetingEntityUserService.addMeeting(meetingEntity);
    }

   @Override
    public ResponseEntity<?> editMeeting(@PathVariable Long id,@RequestBody MeetingEntityRequestDTO meetingEntityDTO){
        return  meetingEntityUserService.editMeetingDetails(id,meetingEntityDTO);
    }

   @Override
    public ResponseEntity<?> getUserMeetingDate( @PathVariable Date date){
        return  meetingEntityUserService.getUserMeetingDetails(date);
    }

    @Override
    public ResponseEntity<?> getCompletedMeeting( @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            LocalDateTime date ){
        return  meetingEntityUserService.getCompletedMeeting(date);
    }

    @Override
    public ResponseEntity<?> getUpcomingMeeting(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                              LocalDateTime date, @PathVariable MeetingStatusEntity status){
        return  meetingEntityUserService.getUpcomingMeetingDetails(date,status);
    }

    @Override
    public ResponseEntity<?> withdrawMeeting(@PathVariable Long id){
        return meetingEntityUserService.withdrawMeeting(id);
    }


}
