package com.divum.MeetingRoomBlocker.Service;


import com.divum.MeetingRoomBlocker.DTO.UserDTO.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthServices {

    public ResponseEntity<?> loginService(LoginDTO loginDTO, String userEnv, HttpServletResponse response);

    public ResponseEntity<?> logoutService(HttpServletRequest request, HttpServletResponse response, String userEnv);

    public ResponseEntity<?> getAccessToken(HttpServletRequest request);
}
