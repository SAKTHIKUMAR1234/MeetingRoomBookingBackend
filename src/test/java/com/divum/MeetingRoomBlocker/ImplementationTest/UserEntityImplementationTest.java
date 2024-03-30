package com.divum.MeetingRoomBlocker.ImplementationTest;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.Implementation.UserEntityImplementation;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserEntityImplementationTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserEntityImplementation userEntityImplementation;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserByEmail(){
        String userEmail = "abc@gmail.com";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Test User");
        userEntity.setEmail(userEmail);

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(Optional.of(userEntity));

        ResponseEntity<?> responseEntity = userEntityImplementation.getUserByEmail();
        assertNotNull(responseEntity);

        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(),responseDTO.getHttpStatus());
        assertEquals(Constants.USER_DETAILS_FETCHED,responseDTO.getMessage());
    }

    @Test
    void userNotFoundTest(){
        String userEmail = "abc@gmail.com";
        when(authentication.getName()).thenReturn(userEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, ()-> userEntityImplementation.getUserByEmail());
    }
}
