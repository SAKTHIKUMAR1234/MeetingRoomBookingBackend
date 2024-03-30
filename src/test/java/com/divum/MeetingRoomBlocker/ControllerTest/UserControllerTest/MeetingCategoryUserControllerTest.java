package com.divum.MeetingRoomBlocker.ControllerTest.UserControllerTest;

import com.divum.MeetingRoomBlocker.Controller.UserController.MeetingCategoryUserController;
import com.divum.MeetingRoomBlocker.Service.UserService.MeetingCategoryEntityService;
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

public class MeetingCategoryUserControllerTest {

    @Mock
    private MeetingCategoryEntityService meetingCategoryEntityService;

    @InjectMocks
    private MeetingCategoryUserController meetingCategoryUserController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInternalAttendeesTest(){
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingCategoryEntityService.getCategories()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingCategoryUserController.getCategories();
        verify(meetingCategoryEntityService).getCategories();
        assertEquals(expectedResponse, actualResponse);
    }

}
