package com.divum.MeetingRoomBlocker.ControllerTest.AdminControllerTest;

import com.divum.MeetingRoomBlocker.Controller.AdminController.MeetingEntityAdminController;
import com.divum.MeetingRoomBlocker.DTO.CategoryDTO.MeetingCategoryDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingDeleteDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Service.AdminService.MeetingEntityAdminService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MeetingEntityAdminControllerTest {

    @Mock
    private MeetingEntityAdminService meetingEntityAdminService;

    @InjectMocks
    private MeetingEntityAdminController meetingEntityAdminController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addMeetingsTest(){
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityAdminService.addMeetings(meetingEntityRequestDTO)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityAdminController.addMeetings(meetingEntityRequestDTO);
        verify(meetingEntityAdminService).addMeetings(meetingEntityRequestDTO);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void deleteMeetingsTest(){
        MeetingDeleteDTO meetingDeleteDTO = new MeetingDeleteDTO();
        List<Long> ids = meetingDeleteDTO.getIds();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityAdminService.deleteMeetingById(ids)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityAdminController.deleteMeetings(meetingDeleteDTO);
        verify(meetingEntityAdminService).deleteMeetingById(ids);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void upcomingMeetingsTest(){
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityAdminService.upcomingMeetingsbyhost()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityAdminController.upcomingmeetings();
        verify(meetingEntityAdminService).upcomingMeetingsbyhost();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void requestsTest(){
        LocalDate date = LocalDate.now();
        ResponseDTO responseDTO = new ResponseDTO();
        ResponseEntity<ResponseDTO> expectedResponse = ResponseEntity.ok(responseDTO);
        Mockito.when(meetingEntityAdminService.requests(date)).thenReturn(new ResponseDTO());
        ResponseEntity<?> actualResponse = meetingEntityAdminController.requests(date);
        verify(meetingEntityAdminService).requests(date);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void unblockTest(){
        Long id = 1L;
        LocalDate date = LocalDate.now();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityAdminService.unblock(id, date)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityAdminController.unblock(id, date);
        verify(meetingEntityAdminService).unblock(id, date);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void upcomingMeetingsByDateTest(){
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityAdminService.upcomingMeetingsbyDate()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityAdminController.upcomingmeetingsbyDate();
        verify(meetingEntityAdminService).upcomingMeetingsbyDate();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void historyTest(){
        LocalDate startdate = LocalDate.now();
        LocalDate enddate = LocalDate.now();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityAdminService.history(startdate, enddate)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityAdminController.history(startdate,enddate);
        verify(meetingEntityAdminService).history(startdate, enddate);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void acceptMeetingTest(){
        Long id = 1L;
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityAdminService.acceptMeeting(id)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityAdminController.acceptmeeting(id);
        verify(meetingEntityAdminService).acceptMeeting(id);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void rejectMeetingTest(){
        Long id = 1L;
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        meetingEntityRequestDTO.setReason("Rejected");
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityAdminService.rejectMeeting(id, meetingEntityRequestDTO)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityAdminController.rejectmeeting(id, meetingEntityRequestDTO);
        verify(meetingEntityAdminService).rejectMeeting(id, meetingEntityRequestDTO);
        assertEquals(expectedResponse, actualResponse);
    }

}
