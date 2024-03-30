package com.divum.MeetingRoomBlocker.ControllerTest.AdminControllerTest;

import com.divum.MeetingRoomBlocker.Controller.AdminController.MeetingCategoryController;
import com.divum.MeetingRoomBlocker.DTO.CategoryDTO.MeetingCategoryDTO;
import com.divum.MeetingRoomBlocker.Service.AdminService.MeetingCategoryServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MeetingCategoryControllerTest {

    @Mock
    private MeetingCategoryServices meetingCategoryServices;

    @InjectMocks
    private MeetingCategoryController meetingCategoryController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFacilityTest(){
        MeetingCategoryDTO meetingCategoryDTO = new MeetingCategoryDTO();
        meetingCategoryDTO.setMeetingCategoryName("Client Meeting");
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingCategoryServices.addCategory(meetingCategoryDTO)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingCategoryController.createMeetingCategory(meetingCategoryDTO);
        verify(meetingCategoryServices).addCategory(meetingCategoryDTO);
        assertEquals(expectedResponse, actualResponse);
    }

}
