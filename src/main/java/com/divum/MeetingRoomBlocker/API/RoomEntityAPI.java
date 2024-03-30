package com.divum.MeetingRoomBlocker.API;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${Rooms_Api}")
public interface RoomEntityAPI {

    @GetMapping("${RoomsDisplay}")
    public ResponseEntity<?> getAllRooms();

    @GetMapping("${RoomsDisplayById}")
    public ResponseEntity<?> getRoomsbyId(@PathVariable("id") Long id);
}
