package com.divum.MeetingRoomBlocker.Service.AdminService;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.RoomDTO.RoomEntityDTO;
import org.springframework.stereotype.Service;

@Service
public interface RoomEntityAdminService {

    ResponseDTO addRoom(RoomEntityDTO roomEntityDTO);

}
