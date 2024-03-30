package com.divum.MeetingRoomBlocker.ControllerTest.AdminControllerTest;

import com.divum.MeetingRoomBlocker.Controller.AdminController.FacilitiesController;
import com.divum.MeetingRoomBlocker.Service.AdminService.FacilitiesServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FacilitiesControllerTest {

    @Mock
    private FacilitiesServices facilitiesServices;

    @InjectMocks
    private FacilitiesController facilitiesController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFacilityTest(){
        MultipartFile multipartFile = mock(MultipartFile.class);
        String FacilityName = "TV";
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(facilitiesServices.addFacility(multipartFile, FacilityName)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = facilitiesController.addFacility(multipartFile, FacilityName);
        verify(facilitiesServices).addFacility(multipartFile, FacilityName);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getAllFacilityTest(){
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(facilitiesServices.getAllFacilities()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = facilitiesController.getAllFacility();
        verify(facilitiesServices).getAllFacilities();
        assertEquals(expectedResponse, actualResponse);
    }

}
