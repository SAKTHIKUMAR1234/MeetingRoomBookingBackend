package com.divum.MeetingRoomBlocker.ImplementationTest;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Service.Implementation.RoomEntityImplementation;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class RoomEntityImplementationTest {

    @Mock
    private RoomEntityRepository roomEntityRepository;

    @InjectMocks
    private RoomEntityImplementation roomEntityImplementation;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoomsTest(){
        RoomEntity room = new RoomEntity();
        room.setId(1L);

        List<RoomEntity> rooms = new ArrayList<>();
        rooms.add(room);

        when(roomEntityRepository.findAll(Sort.by("id").ascending())).thenReturn(rooms);
        ResponseEntity<?> responseEntity = roomEntityImplementation.getAllRooms();
        assertNotNull(responseEntity);

        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(),responseDTO.getHttpStatus());
        assertEquals(Constants.ROOM_FETCHED,responseDTO.getMessage());
        assertEquals(rooms, responseDTO.getData());
    }

    @Test
    void getAllRoomsNotFoundTest(){
        List<RoomEntity> roomEntities = new ArrayList<>();
        when(roomEntityRepository.findAll(Sort.by("id").ascending())).thenReturn(roomEntities);
        assertThrows(DataNotFoundException.class, ()->roomEntityImplementation.getAllRooms());
    }

    @Test
    void getRoomsByIdTest(){
        RoomEntity room = new RoomEntity();
        room.setId(1L);

        when(roomEntityRepository.findById(room.getId())).thenReturn(Optional.of(room));
        ResponseEntity<?> responseEntity = roomEntityImplementation.getRoomsById(room.getId());
        assertNotNull(responseEntity);

        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(),responseDTO.getHttpStatus());
        assertEquals(Constants.ROOM_FETCHED,responseDTO.getMessage());
    }

    @Test
    void getRoomByIdEmptyTest(){
        RoomEntity room = new RoomEntity();
        when(roomEntityRepository.findById(room.getId())).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, ()-> roomEntityImplementation.getRoomsById(room.getId()));
    }
}
