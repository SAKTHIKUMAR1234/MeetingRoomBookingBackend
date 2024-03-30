package com.divum.MeetingRoomBlocker.API;
import com.divum.MeetingRoomBlocker.DTO.UserDTO.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("${Auth_Api}")
public interface AuthAPI {

    @PostMapping("${Login}")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, @RequestHeader("User-Agent") String userEnv,HttpServletResponse response);

    @GetMapping("${Logout}")
    public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response,@RequestHeader("User-Agent") String userEnv);

    @GetMapping("${AccessToken}")
    public ResponseEntity<?> getAccessToken(HttpServletRequest request);


}
