package com.divum.MeetingRoomBlocker.ImplementationTest.UserImplementation;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.Implementation.UserImplementation.UserEntityUserImplementation;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserEntityUserImplementationTest {

    @Mock
    private UserEntityRepository userEntityRepository;
   @InjectMocks
    private UserEntityUserImplementation userEntityUserImplementation;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }
   @Test
    public void internalAttendees_Success(){
       List<UserEntity> userEntities=new ArrayList<>();
       UserEntity user=new UserEntity();
       user.setId(1L);
       user.setName("User 1");
       user.setEmail("user1@divum.in");
       userEntities.add(user);
       when(userEntityRepository.findAll()).thenReturn(userEntities);

       ResponseEntity<?> responseEntity = userEntityUserImplementation.getInternalUsers();
       assertNotNull(responseEntity);

       ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
       assertNotNull(responseDTO);
       assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
       assertEquals(Constants.INTERNAL_ATTENDEES_FETCHED,responseDTO.getMessage());

   }
   @Test
    public void internalAttendee_InternalServerError(){
       ResponseEntity<?> responseEntity = userEntityUserImplementation.getInternalUsers();
       assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
   }


}