package com.divum.MeetingRoomBlocker.ImplementationTest.AdminImplementationTest;

import com.divum.MeetingRoomBlocker.DTO.RoomDTO.ImageUploadResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.RoomDTO.RoomEntityDTO;
import com.divum.MeetingRoomBlocker.Entity.FacilitiesEntity;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import com.divum.MeetingRoomBlocker.Exception.CloudStorageException;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Exception.DuplicateItemError;
import com.divum.MeetingRoomBlocker.Exception.InvalidDataException;
import com.divum.MeetingRoomBlocker.Service.Implementation.AdminImplementation.RoomEntityAdminImplementation;
import com.divum.MeetingRoomBlocker.Repository.FacilitiesEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Service.AwsServices;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoomEntityAdminImpementationTest {

    @Mock
    private RoomEntityRepository roomEntityRepository;

    @Mock
    private FacilitiesEntityRepository facilitiesEntityRepository;

    @Mock
    private AwsServices awsServices;

    @InjectMocks
    private RoomEntityAdminImplementation roomEntityAdminImplementation;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addRoomTest(){
        MultipartFile multipartFile = mock(MultipartFile.class);

        RoomEntityDTO roomEntityDTO = new RoomEntityDTO();
        roomEntityDTO.setName("Room 1");
        roomEntityDTO.setMaxCapacity(50);
        roomEntityDTO.setMinCapacity(10);
        roomEntityDTO.setRoomImage(multipartFile);

        List<String> facilitiesEntityList = new ArrayList<>();
        facilitiesEntityList.add("TV");
        facilitiesEntityList.add("Wifi");
        roomEntityDTO.setFacilitiesEntityList(facilitiesEntityList);

        FacilitiesEntity facility1= new FacilitiesEntity();
        facility1.setFacilityName("TV");

        FacilitiesEntity facility2= new FacilitiesEntity();
        facility2.setFacilityName("Wifi");

        ImageUploadResponseDTO image = new ImageUploadResponseDTO();

        RoomEntity room = new RoomEntity();
        room.setName(roomEntityDTO.getName());
        room.setMinCapacity(roomEntityDTO.getMinCapacity());
        room.setMaxCapacity(roomEntityDTO.getMaxCapacity());
        room.setId(1L);

        when(awsServices.uploadImage(multipartFile)).thenReturn(image);
        when(facilitiesEntityRepository.findByFacilityName(facility1.getFacilityName())).thenReturn(Optional.of(facility1));
        when(facilitiesEntityRepository.findByFacilityName(facility2.getFacilityName())).thenReturn(Optional.of(facility2));
        when(roomEntityRepository.findByName(roomEntityDTO.getName())).thenReturn(Optional.empty());
        when(roomEntityRepository.save(any())).thenReturn(room);

        ResponseDTO responseDTO = roomEntityAdminImplementation.addRoom(roomEntityDTO);
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.CREATED.getReasonPhrase(),responseDTO.getHttpStatus());
        assertEquals(Constants.ROOM_CREATED,responseDTO.getMessage());
    }

    @Test
    void roomDuplicateTest(){
        RoomEntityDTO room = new RoomEntityDTO();
        room.setName("Room 1");

        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(room.getName());

        when(roomEntityRepository.findByName(room.getName())).thenReturn(Optional.of(roomEntity));
        assertThrows(DuplicateItemError.class, ()-> roomEntityAdminImplementation.addRoom(room));
    }

    @Test
    void facilityNotFoundTest() {
        RoomEntityDTO roomDTO = new RoomEntityDTO();
        roomDTO.setName("Room 1");
        roomDTO.setFacilitiesEntityList(List.of("TV"));

        when(facilitiesEntityRepository.findByFacilityName("TV")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> roomEntityAdminImplementation.addRoom(roomDTO));
    }

    @Test
    void repeatedFacilityTest() {
        RoomEntityDTO roomDTO = new RoomEntityDTO();
        roomDTO.setName("Room 1");
        roomDTO.setFacilitiesEntityList(List.of("TV", "TV"));

        FacilitiesEntity facilitiesEntity = new FacilitiesEntity();
        facilitiesEntity.setFacilityName("TV");

        when(facilitiesEntityRepository.findByFacilityName("TV")).thenReturn(Optional.of(facilitiesEntity));

        assertThrows(DuplicateItemError.class, () -> roomEntityAdminImplementation.addRoom(roomDTO));
    }

    @Test
    void capacityErrorTest(){
        FacilitiesEntity facility1= new FacilitiesEntity();
        facility1.setFacilityName("TV");

        RoomEntityDTO room = new RoomEntityDTO();
        room.setMinCapacity(20);
        room.setMaxCapacity(15);
        room.setFacilitiesEntityList(List.of("TV"));

        when(roomEntityRepository.findByName(room.getName())).thenReturn(Optional.empty());
        when(facilitiesEntityRepository.findByFacilityName(facility1.getFacilityName())).thenReturn(Optional.of(facility1));

        assertThrows(InvalidDataException.class, ()-> roomEntityAdminImplementation.addRoom(room));
    }

    @Test
    void imageUploadErrorTest(){
        MultipartFile multipartFile = mock(MultipartFile.class);

        FacilitiesEntity facility1= new FacilitiesEntity();
        facility1.setFacilityName("TV");

        ImageUploadResponseDTO responseDTO = new ImageUploadResponseDTO();
        responseDTO.setError(true);
        responseDTO.setErrorString("Error Message");

        RoomEntityDTO room = new RoomEntityDTO();
        room.setMinCapacity(20);
        room.setMaxCapacity(30);
        room.setFacilitiesEntityList(List.of("TV"));
        room.setRoomImage(multipartFile);

        when(roomEntityRepository.findByName(room.getName())).thenReturn(Optional.empty());
        when(awsServices.uploadImage(multipartFile)).thenReturn(responseDTO);
        when(facilitiesEntityRepository.findByFacilityName(facility1.getFacilityName())).thenReturn(Optional.of(facility1));

        assertThrows(CloudStorageException.class, ()-> roomEntityAdminImplementation.addRoom(room));
    }
}
