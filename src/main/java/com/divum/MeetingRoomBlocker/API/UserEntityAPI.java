package com.divum.MeetingRoomBlocker.API;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${User_Api}")
public interface UserEntityAPI {

    @GetMapping("${UserDetails}")
    public ResponseEntity<?> getUserById();

}
