package com.divum.MeetingRoomBlocker.ImplementationTest.AdminImplementationTest;

import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.CreateEventDto;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityActivitiesDTO;
import com.divum.MeetingRoomBlocker.DTO.MeetingDTO.MeetingEntityRequestDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.UserDTO.InternalUserDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingCategoryEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Service.Implementation.AdminImplementation.MeetingEntityAdminImplementation;
import com.divum.MeetingRoomBlocker.Repository.MeetingCategoryEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Scheduler.TaskTrigger;
import com.divum.MeetingRoomBlocker.Service.CalendarService.CalendarEventService;
import com.divum.MeetingRoomBlocker.Service.MailServices.MailService;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class MeetingAdminImplementationTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private MeetingCategoryEntityRepository meetingCategoryEntityRepository;

    @Mock
    private MeetingEntityRepository meetingEntityRepository;

    @Mock
    private RoomEntityRepository roomEntityRepository;

    @Mock
    private MailService mailService;

    @Mock
    private CalendarEventService calendarEventService;

    @Mock
    private TaskTrigger taskTrigger;


    @InjectMocks
    private MeetingEntityAdminImplementation meetingEntityAdminImplementation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddMeeting() {
        String userEmail = "abc@gmail.com";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        MeetingEntityRequestDTO meetingEntityDTO = new MeetingEntityRequestDTO();
        meetingEntityDTO.setMeetingName("Meeting Test");
        meetingEntityDTO.setDescription("Description Test");
        meetingEntityDTO.setStartTime("2024-03-01T10:00:00");
        meetingEntityDTO.setEndTime("2024-03-01T11:00:00");
        meetingEntityDTO.setStatus(MeetingStatusEntity.ACCEPTED);
        meetingEntityDTO.setMeetingCategory("Client Meeting");
        meetingEntityDTO.setRoomEntityId(1L);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        MeetingCategoryEntity meetingCategory = new MeetingCategoryEntity();
        meetingCategory.setCategoryName("Client Meeting");
        RoomEntity room = new RoomEntity();
        room.setId(1L);
        MeetingEntity meetingEntity = new MeetingEntity();
        when(meetingCategoryEntityRepository.findMeetingCategoryEntities("Client Meeting")).thenReturn(meetingCategory);
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(roomEntityRepository.findById(meetingEntityDTO.getRoomEntityId())).thenReturn(Optional.of(room));
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(meetingEntityRepository.save(any())).thenReturn(meetingEntity);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.addMeetings(meetingEntityDTO);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_BLOCKED, responseDTO.getMessage());
        verify(meetingEntityRepository, times(1)).save(any());
    }

    @Test
    void addMeetings_MEETINGS_PRESENT() {
        String userEmail = "user@divum.in";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        MeetingEntityRequestDTO meetingEntityDTO = new MeetingEntityRequestDTO();
        meetingEntityDTO.setStartTime("2024-03-21T10:00:00");
        meetingEntityDTO.setEndTime("2024-03-21T11:00:00");
        List<MeetingEntity> overlappingMeetings = List.of(new MeetingEntity(), new MeetingEntity());
        when(meetingEntityRepository.findOverlappingMeetings(any(Timestamp.class), any(Timestamp.class), any(MeetingStatusEntity.class), any())).thenReturn(overlappingMeetings);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.addMeetings(meetingEntityDTO);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETINGS_PRESENT, responseDTO.getMessage());
        verify(meetingEntityRepository, times(1)).findOverlappingMeetings(any(Timestamp.class), any(Timestamp.class), any(MeetingStatusEntity.class), any());
    }

    @Test
    void testAddMeeting_INTERNAL_ERROR() {
        String userEmail = "abc@gmail.com";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        MeetingEntityRequestDTO meetingEntityDTO = new MeetingEntityRequestDTO();
        meetingEntityDTO.setMeetingName("Meeting Test");
        meetingEntityDTO.setDescription("Description Test");
        meetingEntityDTO.setStartTime("2024-03-01T10:00:00");
        meetingEntityDTO.setEndTime("2024-03-01T11:00:00");
        meetingEntityDTO.setStatus(MeetingStatusEntity.ACCEPTED);
        meetingEntityDTO.setMeetingCategory("Client Meeting");
        meetingEntityDTO.setRoomEntityId(1L);
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.addMeetings(meetingEntityDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void deleteMeetingById_Success() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setId(1L);
        MeetingEntity meeting2 = new MeetingEntity();
        meeting2.setId(2L);
        List<MeetingEntity> meetings = new ArrayList<>();
        meetings.add(meeting1);
        meetings.add(meeting2);
        when(meetingEntityRepository.findByIdIn(ids)).thenReturn(meetings);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.deleteMeetingById(ids);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_UNBLOCKED, responseDTO.getMessage());
        verify(meetingEntityRepository, times(1)).findByIdIn(ids);
        verify(meetingEntityRepository, times(1)).saveAll(meetings);
    }

    @Test
    void deleteMeetingById_InternalError() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        when(meetingEntityRepository.findByIdIn(ids)).thenThrow(RuntimeException.class);
        ResponseEntity<?> response = meetingEntityAdminImplementation.deleteMeetingById(ids);
        verify(meetingEntityRepository, times(1)).findByIdIn(ids);
        verifyNoMoreInteractions(meetingEntityRepository);
    }

    @Test
    void upcomingMeetingsbyhost_Success() {
        String userEmail = "user@divum.in";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        String email = "user@divum.in";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail(email);
        MeetingEntity meetingEntity1 = new MeetingEntity();
        meetingEntity1.setMeetingName("MRB Meating Room Booking");
        meetingEntity1.setId(1L);
        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(user));
        List<MeetingEntity> hostMeetings = new ArrayList<>();
        hostMeetings.add(meetingEntity1);
        when(meetingEntityRepository.findByHostIdAndStartTimeAfterOrderByStartTime(user.getId(), Timestamp.valueOf(LocalDateTime.now()))).thenReturn(hostMeetings);
        List<MeetingEntity> attendeeMeetings = new ArrayList<>();
        attendeeMeetings.add(meetingEntity1);
        when(meetingEntityRepository.findByGuestListAndStartTimeAfterOrderByStartTime(user, Timestamp.valueOf(LocalDateTime.now()))).thenReturn(attendeeMeetings);
        ResponseEntity<?> response = meetingEntityAdminImplementation.upcomingMeetingsbyhost();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED, responseDTO.getMessage());
    }

    @Test
    void upcomingMeetingsbyhost_UserEmailNotFound() {
        String userEmail = "user@divum.in";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userEntityRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        ResponseEntity<?> response = meetingEntityAdminImplementation.upcomingMeetingsbyhost();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.INTERNAL_ERROR, responseDTO.getMessage());
    }

    @Test
    void requests_Success() {
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        List<MeetingEntity> meetings = new ArrayList<>();
        meetings.add(meeting1);
        when(meetingEntityRepository.findByStartTimeAfterOrderByStartTime(meeting1.getStartTime())).thenReturn(meetings);
        ResponseDTO response = meetingEntityAdminImplementation.requests(LocalDate.now());
        assertNotNull(response);
        assertEquals(HttpStatus.OK.getReasonPhrase(), response.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED, response.getMessage());
        assertNotNull(response.getData());
    }
    @Test
    void requests_ExceptionThrown() {
        LocalDate date = LocalDate.of(2024, 3, 21);
        Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
        when(meetingEntityRepository.findByStartTimeAfterOrderByStartTime(timestamp)).thenThrow(new RuntimeException("Error fetching meetings"));
        ResponseDTO responseDTO = meetingEntityAdminImplementation.requests(date);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.INTERNAL_ERROR, responseDTO.getMessage());
        verify(meetingEntityRepository, times(1)).findByStartTimeAfterOrderByStartTime(timestamp);
    }
    @Test
    void upcomingMeetingsbyDate_Success() {
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        List<MeetingEntity> upcomingMeetings = new ArrayList<>();
        upcomingMeetings.add(meeting1);
        when(meetingEntityRepository.findByStartTimeAfterOrderByStartTime(Timestamp.valueOf(LocalDateTime.now()))).thenReturn(upcomingMeetings);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.upcomingMeetingsbyDate();
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED, responseDTO.getMessage());
        assertNotNull(responseDTO.getData());
    }
    @Test
    void history() {
        List<MeetingEntity> meetings = new ArrayList<>();
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meetings.add(meeting1);
        when(meetingEntityRepository.findByStartDateAndEndDate(Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()))).thenReturn(meetings);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.history(LocalDate.now(), LocalDate.now());
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED, responseDTO.getMessage());
        assertNotNull(responseDTO.getData());
    }
    @Test
    void history_Sucess() {
        List<MeetingEntity> meetings = new ArrayList<>();
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meetings.add(meeting1);
        when(meetingEntityRepository.findByStartDateAndEndDate(any(Timestamp.class), any(Timestamp.class))).thenReturn(meetings);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.history(null, null);
        assertNotNull(responseEntity);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        responseEntity = meetingEntityAdminImplementation.history(LocalDate.now(), LocalDate.now());
        responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        verify(meetingEntityRepository, times(2)).findByStartDateAndEndDate(any(Timestamp.class), any(Timestamp.class));
    }
    @Test
    void testHistoryErrorFetchingMeetings() {
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay().minusNanos(1));
        when(meetingEntityRepository.findByStartDateAndEndDate(startTimestamp, endTimestamp)).thenThrow(new RuntimeException("Error fetching meetings"));
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.history(startDate, endDate);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void unblock_sucess() {
        String userEmail = "user@divum.in";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        String email = "user@divum.in";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail(email);
        user.setName("user");
        UserEntity user1 = new UserEntity();
        user1.setId(2L);
        user1.setEmail("user1@divum.in");
        user1.setName("user1");
        UserEntity user2 = new UserEntity();
        user2.setId(3L);
        user2.setEmail("user2@divum.in");
        user2.setName("user2");
        List<UserEntity> guestlist = new ArrayList<>();
        guestlist.add(user1);
        guestlist.add(user2);
        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(roomEntityRepository.existsById(1L)).thenReturn(true);
        when(userEntityRepository.existsById(2L)).thenReturn(true);
        RoomEntity room = new RoomEntity();
        room.setId(1l);
        room.setName("Room 1");
        List<MeetingEntity> meetings = new ArrayList<>();
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setRoomEntity(room);
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setHost(user);
        meeting1.setGuestList(guestlist);
        meetings.add(meeting1);
        when(meetingEntityRepository.findByRoomEntityIdAndHostIdAndStartTimeBetween(1L, 1L, LocalDateTime.now())).thenReturn(meetings);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.unblock(1L, LocalDate.now());
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED, responseDTO.getMessage());
        assertNotNull(responseDTO.getData());
    }
    @Test
    void unblock_RoomNotFound() {
        String userEmail = "user@divum.in";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        UserEntity user1 = new UserEntity();
        user1.setId(2L);
        user1.setEmail("user1@divum.in");
        user1.setName("user1");
        when(userEntityRepository.findByEmail(userEmail)).thenReturn(Optional.of(user1));
        when(roomEntityRepository.existsById(1L)).thenReturn(false);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.unblock(1L, LocalDate.now());
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.INTERNAL_ERROR, responseDTO.getMessage());
    }
    @Test
    void acceptMeeting_CALENDER_CREATED() {
        String email = "user@divum.in";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail(email);
        user.setName("user");
        UserEntity user1 = new UserEntity();
        user1.setId(2L);
        user1.setEmail("user1@divum.in");
        user1.setName("user1");
        UserEntity user2 = new UserEntity();
        user2.setId(3L);
        user2.setEmail("user2@divum.in");
        user2.setName("user2");
        List<UserEntity> guestlist = new ArrayList<>();
        guestlist.add(user1);
        guestlist.add(user2);
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setId(1L);
        meeting1.setMeetingName("Meeting 1");
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setDescription("This meeting a client meeting");
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setGuestList(guestlist);
        meeting1.setHost(user);
        Long id = 1L;
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting1));
        when(meetingEntityRepository.findOverlappingMeetings(any(Timestamp.class), any(Timestamp.class), eq(MeetingStatusEntity.ACCEPTED), any())).thenReturn(new ArrayList<>());
        when(meetingEntityRepository.save(meeting1)).thenReturn(meeting1);
        when(mailService.SendMailToMeetingAttenders(id)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(calendarEventService.calendarCreateEvent(any(CreateEventDto.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.acceptMeeting(id);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.INTERNAL_ERROR, responseDTO.getMessage());
    }
    @Test
    void acceptMeeting_MEETINGS_PRESENT() {
        Long id = 1L;
        List<MeetingEntity> meetingEntities = new ArrayList<>();
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setId(1L);
        meeting1.setMeetingName("Meeting 1");
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setDescription("This meeting a client meeting");
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meetingEntities.add(meeting1);
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting1));
        when(meetingEntityRepository.findOverlappingMeetings(any(Timestamp.class), any(Timestamp.class), eq(MeetingStatusEntity.ACCEPTED), any())).thenReturn(meetingEntities);
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.acceptMeeting(id);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.INTERNAL_ERROR, responseDTO.getMessage());
    }
    @Test
    void acceptMeeting_INTERNAL_SERVER_ERROR() {
        Long id = 1L;
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.acceptMeeting(id);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.INTERNAL_ERROR, responseDTO.getMessage());
    }
    @Test
    void rejectMeeting_Success_PendingMeeting() {
        Long meetingId = 1L;
        String reason = "Meeting canceled";
        MeetingEntityRequestDTO meetingEntityDTO = new MeetingEntityRequestDTO();
        meetingEntityDTO.setReason(reason);
        MeetingEntity meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setStatus(MeetingStatusEntity.PENDING);
        meetingEntity.setDeleted(false);
        when(meetingEntityRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.rejectMeeting(meetingId, meetingEntityDTO);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_REJECTED, responseDTO.getMessage());
    }

    @Test
    void rejectMeeting_Success_AcceptedMeeting() {
        Long meetingId = 1L;
        MeetingEntityRequestDTO meetingEntityDTO = new MeetingEntityRequestDTO();
        MeetingEntity meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setStatus(MeetingStatusEntity.ACCEPTED);
        meetingEntity.setDeleted(false);
        when(meetingEntityRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.rejectMeeting(meetingId, meetingEntityDTO);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.CALENDER_DELETED, responseDTO.getMessage());
    }
    @Test
    void rejectMeeting_NotFound() {
        Long meetingId = 1L;
        when(meetingEntityRepository.findById(meetingId)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = meetingEntityAdminImplementation.rejectMeeting(meetingId, new MeetingEntityRequestDTO());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.INTERNAL_ERROR, responseDTO.getMessage());
    }
    @Test
    void ConvertToActivitiesDTO() {
        MeetingEntity meetingEntity = new MeetingEntity();
        meetingEntity.setId(1L);
        meetingEntity.setMeetingName("Meeting 1");
        meetingEntity.setDescription("Description of Meeting 1");
        meetingEntity.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meetingEntity.setEndTime(Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
        meetingEntity.setStatus(MeetingStatusEntity.ACCEPTED);
        meetingEntity.setRoomEntity(new RoomEntity());
        meetingEntity.setHost(new UserEntity());
        meetingEntity.setMeetingCategoryEntity(new MeetingCategoryEntity());
        meetingEntity.setGuestList(Collections.emptyList());
        MeetingEntityActivitiesDTO activitiesDTO = meetingEntityAdminImplementation.convertToActivitiesDTO(meetingEntity);
        assertEquals(1L, activitiesDTO.getId());
        assertEquals("Meeting 1", activitiesDTO.getMeetingName());
        assertEquals("Description of Meeting 1", activitiesDTO.getDescription());
    }
    @Test
    void CovertToUserList() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("user@divum.in");
        userEntity.setName("User");
        InternalUserDTO internalUserDTO = meetingEntityAdminImplementation.covertToUserList(userEntity);
        assertEquals("user@divum.in", internalUserDTO.getEmail());
        assertEquals("User", internalUserDTO.getName());
    }
}








