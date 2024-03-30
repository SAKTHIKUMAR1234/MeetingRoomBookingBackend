package com.divum.MeetingRoomBlocker.ControllerTest;

import com.divum.MeetingRoomBlocker.Controller.MeetingEntityController;
import com.divum.MeetingRoomBlocker.Service.MeetingEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class MeetingEntityControllerTest {

    @Mock
    private MeetingEntityService meetingEntityService;

    @InjectMocks
    private MeetingEntityController meetingEntityController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void viewAllSlotsTest(){
        Long id = 1L;
        LocalDate date = LocalDate.now();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityService.viewslots(id, date)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityController.viewallslots(id, date);
        verify(meetingEntityService).viewslots(id, date);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void yearsAndMonthsMeetingsTest(){
        int year = 2024;
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(meetingEntityService.findByYear(year)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = meetingEntityController.yearandmonthmeetings(year);
        verify(meetingEntityService).findByYear(year);
        assertEquals(expectedResponse, actualResponse);
    }

}
