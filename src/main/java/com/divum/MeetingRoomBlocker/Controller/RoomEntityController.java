package com.divum.MeetingRoomBlocker.Controller;

import com.divum.MeetingRoomBlocker.API.RoomEntityAPI;
import com.divum.MeetingRoomBlocker.Service.RoomEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin
public class RoomEntityController implements RoomEntityAPI {

    public final RoomEntityService roomEntityService;

    @Override
    public ResponseEntity<?> getAllRooms() {
        return roomEntityService.getAllRooms();
    }

    @Override
    public ResponseEntity<?> getRoomsbyId(@PathVariable("id") Long id){
        return roomEntityService.getRoomsById(id);
    }
}
