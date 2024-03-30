package com.divum.MeetingRoomBlocker.ControllerTest;

import com.divum.MeetingRoomBlocker.Controller.RoomEntityController;
import com.divum.MeetingRoomBlocker.Service.RoomEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class RoomEntityControllerTest {

    @Mock
    private RoomEntityService roomEntityService;

    @InjectMocks
    private RoomEntityController roomEntityController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoomsTest(){
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(roomEntityService.getAllRooms()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = roomEntityController.getAllRooms();
        verify(roomEntityService).getAllRooms();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getRoomsbyIdTest(){
        Long id = 1L;
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(roomEntityService.getRoomsById(id)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = roomEntityController.getRoomsbyId(id);
        verify(roomEntityService).getRoomsById(id);
        assertEquals(expectedResponse, actualResponse);
    }

}
