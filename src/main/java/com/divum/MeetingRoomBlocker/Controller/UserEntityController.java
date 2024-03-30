package com.divum.MeetingRoomBlocker.Controller;


import com.divum.MeetingRoomBlocker.API.UserEntityAPI;
import com.divum.MeetingRoomBlocker.Service.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UserEntityController implements UserEntityAPI {

    private final UserEntityService userEntityService;

    @Override
    public ResponseEntity<?> getUserById() {
        return userEntityService.getUserByEmail();
    }

}
