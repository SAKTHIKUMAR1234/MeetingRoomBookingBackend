package com.divum.MeetingRoomBlocker.ControllerTest.AdminControllerTest;

import com.divum.MeetingRoomBlocker.Controller.AdminController.RoomEntityAdminController;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.RoomDTO.RoomEntityDTO;
import com.divum.MeetingRoomBlocker.Service.AdminService.RoomEntityAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class RoomEntityAdminControllerTest {

    @Mock
    private RoomEntityAdminService roomEntityAdminService;

    @InjectMocks
    private RoomEntityAdminController roomEntityAdminController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addRoomTest(){
        RoomEntityDTO roomEntityDTO = new RoomEntityDTO();
        ResponseDTO expectedResponse = new ResponseDTO();
        Mockito.when(roomEntityAdminService.addRoom(roomEntityDTO)).thenReturn(expectedResponse);
        ResponseDTO actualResponse = roomEntityAdminController.addRoom(roomEntityDTO);
        verify(roomEntityAdminService).addRoom(roomEntityDTO);
        assertEquals(expectedResponse, actualResponse);
    }
}
