package com.divum.MeetingRoomBlocker.ControllerTest;

import com.divum.MeetingRoomBlocker.Controller.AuthController;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.RoomDTO.RoomEntityDTO;
import com.divum.MeetingRoomBlocker.DTO.UserDTO.LoginDTO;
import com.divum.MeetingRoomBlocker.Service.AuthServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuthControllerTest {

    @Mock
    private AuthServices authServices;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginTest(){
        LoginDTO loginDTO = new LoginDTO();
        String userEnv = "aaa";
        HttpServletResponse response = mock(HttpServletResponse.class);
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(authServices.loginService(loginDTO, userEnv,response)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = authController.login(loginDTO, userEnv, response);
        verify(authServices).loginService(loginDTO, userEnv, response);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void logoutTest(){
        String userEnv = "aaa";
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(authServices.logoutService(request, response, userEnv)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = authController.logout(request, response, userEnv);
        verify(authServices).logoutService(request, response, userEnv);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getAccessTokenTest(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(authServices.getAccessToken(request)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> actualResponse = authController.getAccessToken(request);
        verify(authServices).getAccessToken(request);
        assertEquals(expectedResponse, actualResponse);
    }

}
