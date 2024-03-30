package com.divum.MeetingRoomBlocker.Service;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserEntityService {

    ResponseEntity<?> getUserByEmail();

}
