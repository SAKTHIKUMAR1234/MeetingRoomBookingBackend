package com.divum.MeetingRoomBlocker.ControllerTest;

import com.divum.MeetingRoomBlocker.Controller.UserEntityController;
import com.divum.MeetingRoomBlocker.Service.UserEntityService;
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

public class UserEntityControllerTest {

    @Mock
    private UserEntityService userEntityService;

    @InjectMocks
    private UserEntityController userEntityController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserByIdTest(){
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(userEntityService.getUserByEmail()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = userEntityController.getUserById();
        verify(userEntityService).getUserByEmail();
        assertEquals(expectedResponse, actualResponse);
    }
}
