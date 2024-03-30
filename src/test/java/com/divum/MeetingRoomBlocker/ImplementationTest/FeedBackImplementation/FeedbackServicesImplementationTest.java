package com.divum.MeetingRoomBlocker.ImplementationTest.FeedBackImplementation;


import com.divum.MeetingRoomBlocker.DTO.FeedBackDto.FeedBackDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.*;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;

import com.divum.MeetingRoomBlocker.Exception.CustomExceptionHandler;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Service.Implementation.FeedBackImplementation.FeedbackServicesImplementation;
import com.divum.MeetingRoomBlocker.Repository.*;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeedbackServicesImplementationTest {

    @Mock
    private UserEntityRepository userEntityRepository;
    @Mock
    private MeetingEntityRepository meetingEntityRepository;
    @Mock
    private CustomExceptionHandler customExceptionHandler;
    @InjectMocks
    private FeedbackServicesImplementation feedbackServicesImplementation;

    @Mock
    private FeedbackEntityRepository feedbackEntityRepository;
    private   UserEntity user;
    private  RoomEntity roomEntity;
    private MeetingCategoryEntity meetingCategoryEntity;
    private MeetingEntity meeting;
    private   List<MeetingEntity> meetingEntities;
    private FeedbackEntity feedbackEntity;
    private FeedBackDTO feedBackDTO;
    private Long id;
    private List<FeedbackEntity> feedbackEntityList;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        user=new UserEntity();
        user.setId(1L);
        user.setName("user1");
        user.setEmail("user@divum.in");
        List<UserEntity> userEntities=new ArrayList<>();
        userEntities.add(user);
        roomEntity=new RoomEntity();
        roomEntity.setId(1L);
        roomEntity.setName("Room 1");
        roomEntity.setMaxCapacity(12);
        roomEntity.setMinCapacity(2);
        meetingCategoryEntity=new MeetingCategoryEntity();
        meetingCategoryEntity.setCategoryName("Team Meeting");
        meetingCategoryEntity.setId(1L);
        meeting =new MeetingEntity();
        meeting.setId(1L);
        meeting.setMeetingName("Mrb meeting 1");
        meeting.setHost(user);
        meeting.setStatus(MeetingStatusEntity.PENDING);
        meeting.setRoomEntity(roomEntity);
        meeting.setStartTime(Timestamp.valueOf(LocalDateTime.parse("2024-02-26T10:00:00")));
        meeting.setEndTime(Timestamp.valueOf(LocalDateTime.parse("2024-02-26T12:00:00")));
        meeting.setMeetingCategoryEntity(meetingCategoryEntity);
        meeting.setGuestList(userEntities);

        meetingEntities =new ArrayList<>();
        meetingEntities.add(meeting);
        id=1L;

        feedbackEntity=new FeedbackEntity();
        feedbackEntity.setFeedback("GOOD");
        feedbackEntity.setRating(4);
        feedbackEntity.setMeetingEntity(meeting);
        feedbackEntity.setUserEntity(user);

        feedbackEntityList= new ArrayList<>();
        feedbackEntityList.add(feedbackEntity);

        feedBackDTO =new FeedBackDTO();
        feedBackDTO.setFeedback("GOOD");
        feedBackDTO.setRating(5);
        feedBackDTO.setHostName("user");
        feedBackDTO.setMeetingId(id);


        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@divum.in");
        when(securityContext.getAuthentication()).thenReturn(authentication);

    }

    @Test
    public void testAddFeedback_Success() {
        meeting.setStatus(MeetingStatusEntity.COMPLETED);
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        when(userEntityRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        ResponseEntity<?> responseEntity =feedbackServicesImplementation.SaveFeedBackOfUser (feedBackDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(Constants.FEEDBACK_ADDED,responseDTO.getMessage());
}


    @Test
    public void testGetFeedbackOnDate() {
        Date date=Date.valueOf(LocalDate.now());
        when(feedbackEntityRepository.findBySubmittedDate(date)).thenReturn(feedbackEntityList);
        ResponseEntity<?> responseEntity =feedbackServicesImplementation.getFeedBackOnDate (date);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(Constants.FEEDBACK_FETCHED,responseDTO.getMessage());
    }


    @Test
    public void testGetFeedback() {
        Date  date=Date.valueOf(LocalDate.now());
        when(feedbackEntityRepository.findBySubmittedDate(date)).thenReturn(feedbackEntityList);
        ResponseEntity<?> responseEntity =feedbackServicesImplementation.getFeedBackOnDate (date);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(Constants.FEEDBACK_FETCHED,responseDTO.getMessage());
    }

    @Test
    public void testGetFeedback_ForLastFourFeedback() {
        when(feedbackEntityRepository.findTop4ByOrderByModifiedAtDesc()).thenReturn(feedbackEntityList);
        ResponseEntity<?> responseEntity =feedbackServicesImplementation.GetLastFourofFeedback();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(Constants.FEEDBACK_FETCHED,responseDTO.getMessage());
    }


    @Test
    public void testSaveFeedbackOfUser_DataNotFoundException() {
        long meetingId = 1L;
        FeedBackDTO feedBackDTO = new FeedBackDTO();
        feedBackDTO.setMeetingId(meetingId);
        meeting.setStatus(MeetingStatusEntity.ACCEPTED);
        when(meetingEntityRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        when(customExceptionHandler.HandleDataNotFoundException(any(DataNotFoundException.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        ResponseEntity<?> response = feedbackServicesImplementation.SaveFeedBackOfUser(feedBackDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customExceptionHandler).HandleDataNotFoundException(any(DataNotFoundException.class));
    }
}