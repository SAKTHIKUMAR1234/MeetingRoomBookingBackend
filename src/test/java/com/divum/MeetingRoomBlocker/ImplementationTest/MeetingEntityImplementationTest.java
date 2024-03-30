package com.divum.MeetingRoomBlocker.ImplementationTest;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.Enum.RoleEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingCategoryEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.Implementation.MeetingEntityImplementation;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeetingEntityImplementationTest {

    @Mock
    private MeetingEntityRepository meetingEntityRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private RoomEntityRepository roomEntityRepository;

    @InjectMocks
    private MeetingEntityImplementation meetingEntityImplementation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void viewslotsTest() {
        RoomEntity room = new RoomEntity();
        room.setId(1L);
        room.setDeleted(false);
        UserEntity user = new UserEntity();
        user.setEmail("abc@gmail.com");
        user.setName("abc");
        MeetingCategoryEntity meetingCategory = new MeetingCategoryEntity();
        meetingCategory.setCategoryName("Client Meeting");
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setMeetingCategoryEntity(meetingCategory);
        meeting1.setRoomEntity(room);
        meeting1.setHost(user);
        MeetingEntity meeting2 = new MeetingEntity();
        meeting2.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting2.setDeleted(false);
        meeting2.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting2.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting2.setMeetingCategoryEntity(meetingCategory);
        meeting2.setRoomEntity(room);
        meeting2.setHost(user);
        Optional<RoomEntity> roomEntityOptional = Optional.of(room);
        when(roomEntityRepository.findById(room.getId())).thenReturn(roomEntityOptional);
        when(meetingEntityRepository.findByRoomEntityIdAndStartTimeBetween(1L, LocalDate.now())).thenReturn(List.of(meeting1, meeting2));
        ResponseEntity<?> responseEntity = meetingEntityImplementation.viewslots(1L, LocalDate.now());
        assertNotNull(responseEntity);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED, responseDTO.getMessage());
    }

    @Test
    void viewslots_ROOM_NOT_FOUND() {
        long roomId = 1L;
        when(roomEntityRepository.findById(roomId)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity1 = meetingEntityImplementation.viewslots(roomId, LocalDate.now());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity1.getStatusCode());
    }

    @Test
    void findByYear_Success_Employee() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@divum.in");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@divum.in");
        user.setName("abc");
        user.setRole(RoleEntity.EMPLOYEE);
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(1L);
        roomEntity.setName("Room 1");
        roomEntity.setMaxCapacity(12);
        roomEntity.setMinCapacity(2);
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setHost(user);
        meeting1.setRoomEntity(roomEntity);
        Optional<UserEntity> userEntityOptional = Optional.of(user);
        List<MeetingEntity> meetings = new ArrayList<>();
        meetings.add(meeting1);
        when(userEntityRepository.findByEmail(user.getEmail())).thenReturn(userEntityOptional);
        when(meetingEntityRepository.findByYearByUser(user.getId(), 2002)).thenReturn(meetings);
        ResponseEntity<?> responseEntity = meetingEntityImplementation.findByYear(2002);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED, responseDTO.getMessage());
    }

    @Test
    void findByYear_Success_NonEmployee() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@divum.in");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@divum.in");
        user.setName("abc");
        user.setRole(RoleEntity.ADMIN);
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(1L);
        roomEntity.setName("Room 1");
        roomEntity.setMaxCapacity(12);
        roomEntity.setMinCapacity(2);
        MeetingEntity meeting1 = new MeetingEntity();
        meeting1.setStatus(MeetingStatusEntity.ACCEPTED);
        meeting1.setDeleted(false);
        meeting1.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        meeting1.setHost(user);
        meeting1.setRoomEntity(roomEntity);
        List<MeetingEntity> meetings = new ArrayList<>();
        meetings.add(meeting1);
        Optional<UserEntity> userEntityOptional = Optional.of(user);
        when(userEntityRepository.findByEmail(user.getEmail())).thenReturn(userEntityOptional);
        when(meetingEntityRepository.findByYear(2002)).thenReturn(meetings);
        ResponseEntity<?> responseEntity = meetingEntityImplementation.findByYear(2002);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.MEETING_FETCHED, responseDTO.getMessage());
    }

    @Test
    void findByYear_UserNotFound() {
        when(userEntityRepository.findByEmail("user@divum.in")).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = meetingEntityImplementation.findByYear(2002);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
