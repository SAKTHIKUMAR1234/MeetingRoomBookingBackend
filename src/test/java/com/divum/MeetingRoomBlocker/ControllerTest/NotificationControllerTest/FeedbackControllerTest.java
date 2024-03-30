package com.divum.MeetingRoomBlocker.ControllerTest.NotificationControllerTest;

import com.divum.MeetingRoomBlocker.Controller.NotificationController.FeedBackController;
import com.divum.MeetingRoomBlocker.DTO.FeedBackDto.FeedBackDTO;
import com.divum.MeetingRoomBlocker.Service.FeedBackServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.bytebuddy.asm.Advice;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FeedbackControllerTest {

    @Mock
    private FeedBackServices feedBackServices;

    @InjectMocks
    private FeedBackController feedBackController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveFeedbackTest(){
        FeedBackDTO feedBackDTO = new FeedBackDTO();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(feedBackServices.SaveFeedBackOfUser(feedBackDTO)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = feedBackController.Savefeedback(feedBackDTO);
        verify(feedBackServices).SaveFeedBackOfUser(feedBackDTO);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void GetAllFeedbackTest(){
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(feedBackServices.GetLastFourofFeedback()).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = feedBackController.GetAllFeedBack();
        verify(feedBackServices).GetLastFourofFeedback();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void GetFeedbackofDateTest(){
        Date date = Date.valueOf(LocalDate.now());
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(feedBackServices.getFeedBackOnDate(date)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = feedBackController.getFeedBackOfDate(date);
        verify(feedBackServices).getFeedBackOnDate(date);
        assertEquals(expectedResponse, actualResponse);
    }

}
