package com.divum.MeetingRoomBlocker.Service;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoomEntityService {

    ResponseEntity<?> getAllRooms();

    ResponseEntity<?> getRoomsById(Long id);
}
