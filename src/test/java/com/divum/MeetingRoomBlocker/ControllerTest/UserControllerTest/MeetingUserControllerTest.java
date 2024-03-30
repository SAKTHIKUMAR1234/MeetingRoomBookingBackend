package com.divum.MeetingRoomBlocker.ControllerTest.UserControllerTest;

import com.divum.MeetingRoomBlocker.Controller.UserController.MeetingUserController;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Service.UserService.MeetingEntityUserService;
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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class MeetingUserControllerTest {

    @Mock
    private MeetingEntityUserService meetingEntityUserService;

    @InjectMocks
    private MeetingUserController meetingUserController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void roomBookingTest(){
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityUserService.addMeeting(meetingEntityRequestDTO)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingUserController.roomBooking(meetingEntityRequestDTO);
        verify(meetingEntityUserService).addMeeting(meetingEntityRequestDTO);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void editMeetingTest(){
        Long id = 1L;
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityUserService.editMeetingDetails(id, meetingEntityRequestDTO)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingUserController.editMeeting(id, meetingEntityRequestDTO);
        verify(meetingEntityUserService).editMeetingDetails(id, meetingEntityRequestDTO);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserMeetingTest(){
        Date date = Date.valueOf(LocalDate.now());
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityUserService.getUserMeetingDetails(date)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingUserController.getUserMeetingDate(date);
        verify(meetingEntityUserService).getUserMeetingDetails(date);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getCompletedMeetingTest(){
        LocalDateTime date = LocalDateTime.now();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityUserService.getCompletedMeeting(date)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingUserController.getCompletedMeeting(date);
        verify(meetingEntityUserService).getCompletedMeeting(date);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUpcomingMeetingTest(){
        LocalDateTime date = LocalDateTime.now();
        MeetingStatusEntity meetingStatusEntity = MeetingStatusEntity.ACCEPTED;
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityUserService.getUpcomingMeetingDetails(date, meetingStatusEntity)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingUserController.getUpcomingMeeting(date, meetingStatusEntity);
        verify(meetingEntityUserService).getUpcomingMeetingDetails(date, meetingStatusEntity);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void withdrawMeetingTest(){
        Long id = 1L;
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityUserService.withdrawMeeting(id)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingUserController.withdrawMeeting(id);
        verify(meetingEntityUserService).withdrawMeeting(id);
        assertEquals(expectedResponse, actualResponse);
    }

}
