package com.divum.MeetingRoomBlocker.ControllerTest.UserControllerTest;

import com.divum.MeetingRoomBlocker.Controller.UserController.AttendeesUserController;
import com.divum.MeetingRoomBlocker.Service.UserService.UserEntityUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class AttendeesUserControllerTest {

    @Mock
    private UserEntityUserService userEntityUserService;

    @InjectMocks
    private AttendeesUserController attendeesUserController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInternalAttendeesTest(){
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(userEntityUserService.getInternalUsers()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = attendeesUserController.getInternalAttendees();
        verify(userEntityUserService).getInternalUsers();
        assertEquals(expectedResponse, actualResponse);
    }
}
