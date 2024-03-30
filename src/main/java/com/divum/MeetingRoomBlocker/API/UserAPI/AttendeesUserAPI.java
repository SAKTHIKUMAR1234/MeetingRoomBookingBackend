package com.divum.MeetingRoomBlocker.API.UserAPI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${Attendees_Api}")
public interface AttendeesUserAPI {

    @GetMapping("${InternalAttendees}")
    public ResponseEntity<?> getInternalAttendees();

}
