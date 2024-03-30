package com.divum.MeetingRoomBlocker.ImplementationTest.UserImplementation;

import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.DeleteEventDto;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.*;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Service.Implementation.CalendarImplementation.CalendarEventImplementation;
import com.divum.MeetingRoomBlocker.Service.Implementation.MailImplementation.MailServicesImplementation;
import com.divum.MeetingRoomBlocker.Repository.*;
import com.divum.MeetingRoomBlocker.Service.Implementation.UserImplementation.MeetingEntityUserImplementation;
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

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeetingEntityUserImplementationTest {

    @Mock
    private UserEntityRepository  userEntityRepository;
    @Mock
    private RoomEntityRepository roomEntityRepository;

    @Mock
    private MeetingCategoryEntityRepository meetingCategoryEntityRepository;
    @Mock
    private FeedbackEntityRepository feedbackEntityRepository;
    @Mock
    private MeetingEntityRepository meetingEntityRepository;
    @InjectMocks
    private MeetingEntityUserImplementation meetingService;
    @Mock
    private MailServicesImplementation mailService;
    @Mock
    private CalendarEventImplementation calendarEventImplementation;

    private   UserEntity user;
    private  RoomEntity roomEntity;
    private MeetingCategoryEntity meetingCategoryEntity;
    private MeetingEntity meeting;
    private LocalDateTime localDateTime;
   private   List<MeetingEntity> meetingEntities;
   private FeedbackEntity feedbackEntity;
   private Long id;


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



        localDateTime=LocalDateTime.now();

        meetingEntities =new ArrayList<>();
        meetingEntities.add(meeting);
         id=1L;

         feedbackEntity=new FeedbackEntity();
         feedbackEntity.setFeedback("GOOD");

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@divum.in");
        when(securityContext.getAuthentication()).thenReturn(authentication);


    }
    @Test
    public void testAddMeeting_Success() {

        String actualEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        assertEquals("user@divum.in", actualEmail);
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        meetingEntityRequestDTO.setMeetingName("Test Meeting");
        meetingEntityRequestDTO.setStartTime("2024-02-26T10:00:00");
        meetingEntityRequestDTO.setEndTime("2024-02-26T12:00:00");
        meetingEntityRequestDTO.setMeetingCategory("Test Category");
        meetingEntityRequestDTO.setDescription("Test Description");
        meetingEntityRequestDTO.setStatus(MeetingStatusEntity.PENDING);
        meetingEntityRequestDTO.setRoomEntityId(id);
        meetingEntityRequestDTO.setMeetingCategory("Team Meeting");
        List<String> emailList=new ArrayList<>();
        emailList.add("user@divum.in");
        List<UserEntity> userEntities=new ArrayList<>();
        userEntities.add(user);
        meetingEntityRequestDTO.setUserEntityList(emailList);
        Timestamp startTimestamp = Timestamp.valueOf(LocalDateTime.parse("2024-02-26T10:00:00"));
        Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.parse("2024-02-26T12:00:00"));
        int test=startTimestamp.compareTo(endTimestamp);
        assertTrue(test<0);
        when(userEntityRepository.findByEmailIn(emailList)).thenReturn(userEntities);
        when(userEntityRepository.findByEmail(actualEmail)).thenReturn(Optional.of(user));
        when(roomEntityRepository.findById(1L)).thenReturn(Optional.of(roomEntity));
        when(meetingCategoryEntityRepository.findMeetingCategoryEntities("Team meeting")).thenReturn(meetingCategoryEntity);
        when(meetingEntityRepository.save(any())).thenReturn(meeting);
        ResponseEntity<?> responseEntity = meetingService.addMeeting(meetingEntityRequestDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_ADDED, responseDTO.getMessage());
        assertEquals(Constants.MEETING_ADDED, responseDTO.getData());
    }

    @Test
    public void AddMeeting_InternalServerError(){
        Date  date=Date.valueOf("2024-03-11");
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        meetingEntityRequestDTO.setMeetingName("Test Meeting");
        meetingEntityRequestDTO.setStartTime("2024-02-26T10:00:00");
        meetingEntityRequestDTO.setEndTime("2024-02-26T12:00:00");
        meetingEntityRequestDTO.setMeetingCategory("Test Category");
        meetingEntityRequestDTO.setDescription("Test Description");
        meetingEntityRequestDTO.setStatus(MeetingStatusEntity.PENDING);
        meetingEntityRequestDTO.setRoomEntityId(id);
        meetingEntityRequestDTO.setMeetingCategory("Team Meeting");

        List<String> emailList=new ArrayList<>();
        emailList.add("user@divum.in");
        List<UserEntity> userEntities=new ArrayList<>();
        userEntities.add(user);
        meetingEntityRequestDTO.setUserEntityList(emailList);
        ResponseEntity<?> responseEntity = meetingService.addMeeting(meetingEntityRequestDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ResponseEntity<?> responseEntity1 = meetingService.getUserMeetingDetails(date);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity1.getStatusCode());

        ResponseEntity<?> responseEntity2 = meetingService.getUpcomingMeetingDetails(localDateTime, MeetingStatusEntity.valueOf("ACCEPTED"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity2.getStatusCode());

        ResponseEntity<?> responseEntity3 = meetingService.getCompletedMeeting(localDateTime);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity3.getStatusCode());

        ResponseEntity<?> responseEntity4 = meetingService.withdrawMeeting(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity4.getStatusCode());




    }

    @Test
    public void editMeeting_Success(){

        String actualEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        assertEquals("user@divum.in", actualEmail);
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        meetingEntityRequestDTO.setMeetingName("Test Meeting");
        meetingEntityRequestDTO.setStartTime("2024-02-26T10:00:00");
        meetingEntityRequestDTO.setEndTime("2024-02-26T12:00:00");
        meetingEntityRequestDTO.setMeetingCategory("Test Category");
        meetingEntityRequestDTO.setDescription("Test Description");
        meetingEntityRequestDTO.setStatus(MeetingStatusEntity.PENDING);
        meetingEntityRequestDTO.setRoomEntityId(id);
        meetingEntityRequestDTO.setMeetingCategory("Team Meeting");
        List<String> emailList=new ArrayList<>();
        emailList.add("user@divum.in");
        List<UserEntity> userEntities=new ArrayList<>();
        userEntities.add(user);
        meetingEntityRequestDTO.setUserEntityList(emailList);
        Timestamp startTimestamp = Timestamp.valueOf(LocalDateTime.parse("2024-02-26T10:00:00"));
        Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.parse("2024-02-26T12:00:00"));
        int test=startTimestamp.compareTo(endTimestamp);


        when(userEntityRepository.findByEmailIn(emailList)).thenReturn(userEntities);
        when(userEntityRepository.findByEmail(actualEmail)).thenReturn(Optional.of(user));
        when(roomEntityRepository.findById(id)).thenReturn(Optional.of(roomEntity));
        when(meetingCategoryEntityRepository.findMeetingCategoryEntities("Team meeting")).thenReturn(meetingCategoryEntity);
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        when(meetingEntityRepository.save(meeting)).thenReturn(meeting);
        ResponseEntity<?> responseEntity = meetingService.editMeetingDetails(id,meetingEntityRequestDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_ADDED, responseDTO.getMessage());
        assertEquals(Constants.MEETING_ADDED, responseDTO.getData());

    }


    @Test
    public void testEditMeeting_NotAllowed() {
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        meetingEntityRequestDTO.setMeetingName("Test Meeting");
        meetingEntityRequestDTO.setStartTime("2024-02-26T10:00:00");
        meetingEntityRequestDTO.setEndTime("2024-02-26T12:00:00");
        meetingEntityRequestDTO.setMeetingCategory("Test Category");
        meetingEntityRequestDTO.setDescription("Test Description");
        meetingEntityRequestDTO.setStatus(MeetingStatusEntity.PENDING);
        meetingEntityRequestDTO.setRoomEntityId(1L);
        meetingEntityRequestDTO.setMeetingCategory("Team Meeting");
        List<String> emailList=new ArrayList<>();
        emailList.add("user@divum.in");
        List<UserEntity> userEntities=new ArrayList<>();
        userEntities.add(user);
        meetingEntityRequestDTO.setUserEntityList(emailList);

        MeetingEntity meeting = mock(MeetingEntity.class);
        when(meeting.getStatus()).thenReturn(MeetingStatusEntity.valueOf("ACCEPTED"));
        when(userEntityRepository.findByEmailIn(emailList)).thenReturn(userEntities);
        when(userEntityRepository.findByEmail("user@divum.in")).thenReturn(Optional.of(user));
        when(roomEntityRepository.findById(1L)).thenReturn(Optional.of(roomEntity));
        when(meetingCategoryEntityRepository.findMeetingCategoryEntities("Team meeting")).thenReturn(meetingCategoryEntity);
        when(meetingEntityRepository.findById(1L)).thenReturn(Optional.of(meeting));

        UserEntity user = mock(UserEntity.class);
        when(user.getEmail()).thenReturn("user@example.com");

        ResponseEntity<?> responseEntity = meetingService.editMeetingDetails(1L,meetingEntityRequestDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(Constants.EDIT_PERMISSION, responseDTO.getMessage());
        verify(meetingEntityRepository, never()).save(any());
    }

    @Test
    public void editMeeting_InternalServerError(){
        MeetingEntityRequestDTO meetingEntityRequestDTO = new MeetingEntityRequestDTO();
        meetingEntityRequestDTO.setMeetingName("Test Meeting");
        meetingEntityRequestDTO.setStartTime("2024-02-26T10:00:00");
        meetingEntityRequestDTO.setEndTime("2024-02-26T12:00:00");
        meetingEntityRequestDTO.setMeetingCategory("Test Category");
        meetingEntityRequestDTO.setDescription("Test Description");
        meetingEntityRequestDTO.setStatus(MeetingStatusEntity.PENDING);
        meetingEntityRequestDTO.setRoomEntityId(1L);
        meetingEntityRequestDTO.setMeetingCategory("Team Meeting");

        List<String> emailList=new ArrayList<>();
        emailList.add("user@divum.in");
        List<UserEntity> userEntities=new ArrayList<>();
        userEntities.add(user);


        ResponseEntity<?> responseEntity = meetingService.editMeetingDetails(1L,meetingEntityRequestDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
   @Test
    public  void getUserMeetingDetails(){
       Date  date=Date.valueOf("2024-03-11");
       String actualEmail = SecurityContextHolder.getContext().getAuthentication().getName();
       assertEquals("user@divum.in", actualEmail);
       when(userEntityRepository.findByEmail(actualEmail)).thenReturn(Optional.of(user));
       when(meetingEntityRepository.findMeetingsByUserEmailAndDate(date, MeetingStatusEntity.valueOf("ACCEPTED"),MeetingStatusEntity.valueOf("COMPLETED"),1L)).thenReturn(meetingEntities);

       ResponseEntity<?> responseEntity = meetingService.getUserMeetingDetails(date);
       assertNotNull(responseEntity);

       ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
       assertNotNull(responseDTO);
       assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
       assertEquals(Constants.MEETING_FETCHED,responseDTO.getMessage());

   }



    @Test
    public void getUpcomingMeetings_Accepted(){

       String actualEmail = SecurityContextHolder.getContext().getAuthentication().getName();
       assertEquals("user@divum.in", actualEmail);
       MeetingStatusEntity status=MeetingStatusEntity.ACCEPTED;
       when(userEntityRepository.findByEmail(actualEmail)).thenReturn(Optional.of(user));
       when(meetingEntityRepository.findByHostIdAndDateAndUpcomingMeeting(localDateTime,status,1L)).thenReturn(meetingEntities);
       ResponseEntity<?> responseEntity = meetingService.getUpcomingMeetingDetails(localDateTime, status);
        assertNotNull(responseEntity);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED,responseDTO.getMessage());
        assertNotNull(responseDTO.getData());

   }

    @Test
    public void getUpcomingMeetings_Pending(){
        LocalDateTime localDateTime1=null;
        String actualEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        assertEquals("user@divum.in", actualEmail);
        MeetingStatusEntity status=MeetingStatusEntity.PENDING;
        when(userEntityRepository.findByEmail(actualEmail)).thenReturn(Optional.of(user));
        when(meetingEntityRepository.findByHostIdAndPendingMeeting(localDateTime,status,1L)).thenReturn(meetingEntities);
        ResponseEntity<?> responseEntity = meetingService.getUpcomingMeetingDetails(localDateTime1, status);
        assertNotNull(responseEntity);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED,responseDTO.getMessage());
        assertNotNull(responseDTO.getData());
    }
    @Test
    public void getCompletedMeeting(){
        LocalDateTime localDateTime1=null ;
        String actualEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        assertEquals("user@divum.in", actualEmail);
        MeetingStatusEntity status=MeetingStatusEntity.REJECTED;
        MeetingStatusEntity status1=MeetingStatusEntity.COMPLETED;
        when(userEntityRepository.findByEmail(actualEmail)).thenReturn(Optional.of(user));
        when(meetingEntityRepository.findByHostIdAndDateAndCompletedMeeting(localDateTime,status1,status,1L)).thenReturn(meetingEntities);
        when(feedbackEntityRepository.findByMeetingEntity(meeting)).thenReturn(Optional.of(feedbackEntity));
        ResponseEntity<?> responseEntity = meetingService.getCompletedMeeting(localDateTime1);
        assertNotNull(responseEntity);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED,responseDTO.getMessage());
        assertNotNull(responseDTO.getData());
    }





    @Test
    public void testWithdrawMeeting_success() {
        meeting.setStatus(MeetingStatusEntity.ACCEPTED);
        when(calendarEventImplementation.calendarDeleteEvent(any(),any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        ResponseEntity<?> responseEntity = meetingService.withdrawMeeting(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testWithdrawMeeting_successForPending() {

        when(calendarEventImplementation.calendarDeleteEvent(any(),any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        ResponseEntity<?> responseEntity = meetingService.withdrawMeeting(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testWithdrawMeeting_Unauthorized() {
        user.setEmail("user1@divum.in");
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        ResponseEntity<?> responseEntity = meetingService.withdrawMeeting(id);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }
    @Test
    public void testWithdrawMeeting_CalendarError(){
        meeting.setStatus(MeetingStatusEntity.ACCEPTED);
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting)).thenReturn(Optional.of(meeting));
        when(mailService.SendMailToMeetingAttenders(id)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(calendarEventImplementation.calendarDeleteEvent(any(),any())).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        ResponseEntity<?> responseEntity = meetingService.withdrawMeeting(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(Constants.CALENDER_ERROR,responseDTO.getMessage());

    }

    private ResponseEntity<?> invokePrivateDeleteEventFromCalendar(MeetingEntity meeting) throws Exception {
        Method method = MeetingEntityUserImplementation.class.getDeclaredMethod("deleteEventFromCalendar", MeetingEntity.class);
        method.setAccessible(true);
        return (ResponseEntity<?>) method.invoke(meetingService, meeting);
    }

    @Test
    public void testDeleteEventFromCalendar_Failure() throws Exception {

        MeetingEntity meeting = new MeetingEntity();
        meeting.setOriginalMeetingId("event_id");
        when(calendarEventImplementation.calendarDeleteEvent(anyString(), any(DeleteEventDto.class)))
                .thenThrow(new RuntimeException(""));
        ResponseEntity<?> response = invokePrivateDeleteEventFromCalendar(meeting);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

}