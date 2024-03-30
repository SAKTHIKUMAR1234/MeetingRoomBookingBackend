package com.divum.MeetingRoomBlocker.ImplementationTest.MailImplementation;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.*;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.Enum.RoleEntity;
import com.divum.MeetingRoomBlocker.Exception.CustomExceptionHandler;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.Implementation.MailImplementation.MailServicesImplementation;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MailServicesImplementationTest {

  @Mock
  private JavaMailSender javaMailSender;
  @Mock
  private MeetingEntityRepository meetingEntityRepository;
  @Mock
  private CustomExceptionHandler customExceptionHandler;
  @Mock
  private RoomEntityRepository roomEntityRepository;
  @Mock
  private UserEntityRepository userEntityRepository;
  @InjectMocks
  private MailServicesImplementation mailServicesImplementation;
  private   UserEntity user;
  private  RoomEntity roomEntity;
  private MeetingCategoryEntity meetingCategoryEntity;
    private MeetingEntity meeting;
    private   List<MeetingEntity> meetingEntities;
    private Long id;
    private List<UserEntity> userEntities;
    private String email;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        user=new UserEntity();
        user.setId(1L);
        user.setName("user1");
        user.setEmail("user@divum.in");
        user.setRole(RoleEntity.EMPLOYEE);
        userEntities=new ArrayList<>();
        userEntities.add(user);
        user=new UserEntity();
        user.setId(2L);
        user.setName("user2");
        user.setEmail("user2@divum.in");
        userEntities.add(user);
        roomEntity=new RoomEntity();
        roomEntity.setId(1L);
        roomEntity.setName("Room 1");
        meetingCategoryEntity=new MeetingCategoryEntity();
        meetingCategoryEntity.setCategoryName("Team Meeting");
        meetingCategoryEntity.setId(1L);
        meeting =new MeetingEntity();
        meeting.setId(1L);
        meeting.setMeetingName("Mrb meeting 1");
        meeting.setHost(user);
        meeting.setStatus(MeetingStatusEntity.PENDING);
        meeting.setRoomEntity(roomEntity);
        meeting.setMeetingCategoryEntity(meetingCategoryEntity);
        meeting.setGuestList(userEntities);
        meeting.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meetingEntities =new ArrayList<>();
        meetingEntities.add(meeting);
         id=1L;
         email="user@divum.in";
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@divum.in");
        when(securityContext.getAuthentication()).thenReturn(authentication);

    }
    @Test
    public void testSendMailToMeetingAttenders_MeetingAccepted() {
       meeting.setStatus(MeetingStatusEntity.ACCEPTED);

        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(meetingEntityRepository.findGuestListByMeetingId(id)).thenReturn(userEntities);
        when(roomEntityRepository.findById(any())).thenReturn(Optional.of(roomEntity));
        ResponseEntity<?> responseEntity = mailServicesImplementation.SendMailToMeetingAttenders(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));
        assertEquals(Constants.ACCEPTED_EMAIL, responseDTO.getMessage());
    }
    @Test
    public void testSendMailToMeetingAttenders_MeetingCancelledByAdmin() {
        meeting.setStatus(MeetingStatusEntity.REJECTED);
        user.setRole(RoleEntity.ADMIN);
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(meetingEntityRepository.findGuestListByMeetingId(id)).thenReturn(userEntities);
        when(roomEntityRepository.findById(any())).thenReturn(Optional.of(roomEntity));
        ResponseEntity<?> responseEntity = mailServicesImplementation.SendMailToMeetingAttenders(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));
        assertEquals(Constants.ADMIN_CANCELLATION_EMAIL, responseDTO.getMessage());
    }
    @Test
    public void testSendMailToMeetingAttenders_MeetingCancelled() {
        meeting.setStatus(MeetingStatusEntity.CANCELLED);
        user.setRole(RoleEntity.EMPLOYEE);
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(meetingEntityRepository.findGuestListByMeetingId(id)).thenReturn(userEntities);
        when(roomEntityRepository.findById(any())).thenReturn(Optional.of(roomEntity));
        ResponseEntity<?> responseEntity = mailServicesImplementation.SendMailToMeetingAttenders(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
        assertEquals(Constants.CANCELLATION_EMAIL, responseDTO.getMessage());
    }

    @Test
    public void testSendMailToMeetingAttenders_AdminMeetingCancelled() {
        meeting.setStatus(MeetingStatusEntity.REJECTED);
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        when(roomEntityRepository.findById(any())).thenReturn(Optional.of(roomEntity));
        when(userEntityRepository.findById(any())).thenReturn(Optional.of(user));
        ResponseEntity<?> responseEntity = mailServicesImplementation.MeetingCancel(1L,"admin canceled");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
        assertEquals(Constants.ADMIN_CANCELLATION_EMAIL, responseDTO.getMessage());
    }

    @Test
    public void testWithdrawMeetingDataNotFound() {
        when(meetingEntityRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = mailServicesImplementation.MeetingCancel(1L,"admin Cancelled");
        verify(customExceptionHandler).HandleDataNotFoundException(any(DataNotFoundException.class));
    }

    @Test
    public void testSaveFeedbackOfUser_DataNotFoundException() {
        meeting.setStatus(MeetingStatusEntity.PENDING);
        user.setRole(RoleEntity.EMPLOYEE);

        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(meetingEntityRepository.findGuestListByMeetingId(id)).thenReturn(userEntities);
        when(meetingEntityRepository.findById(id)).thenReturn(Optional.of(meeting));
        when(customExceptionHandler.HandleDataNotFoundException(any(DataNotFoundException.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        ResponseEntity<?> response = mailServicesImplementation.SendMailToMeetingAttenders(1L);;
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customExceptionHandler).HandleDataNotFoundException(any(DataNotFoundException.class));
    }



}