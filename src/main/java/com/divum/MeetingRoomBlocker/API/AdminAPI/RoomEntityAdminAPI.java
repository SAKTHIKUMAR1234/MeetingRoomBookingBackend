package com.divum.MeetingRoomBlocker.API.AdminAPI;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.RoomDTO.RoomEntityDTO;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${RoomsAdmin_Api}")
public interface RoomEntityAdminAPI {

    @PostMapping("${AddApi}")
    public ResponseDTO addRoom(@ModelAttribute  RoomEntityDTO roomEntityDTO) ;

}
