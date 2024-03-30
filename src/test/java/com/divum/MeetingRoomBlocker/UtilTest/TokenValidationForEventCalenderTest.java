package com.divum.MeetingRoomBlocker.UtilTest;

import com.divum.MeetingRoomBlocker.Config.Oauth2Config;
import com.divum.MeetingRoomBlocker.Util.TokenValidationForEventCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class TokenValidationForEventCalenderTest {

    @Mock
    private Oauth2Config oauth2Config;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TokenValidationForEventCalendar tokenValidationForEventCalendar;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsAccessTokenValid_InvalidToken() {
        when(oauth2Config.getClientId()).thenReturn("testClientId");
        when(oauth2Config.getClientSecret()).thenReturn("testClientSecret");

        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

        assertFalse(tokenValidationForEventCalendar.isAccessTokenValid("invalidAccessToken"));
    }

    @Test
    public void testIsAccessTokenValid_Exception() {
        when(oauth2Config.getClientId()).thenReturn("testClientId");
        when(oauth2Config.getClientSecret()).thenReturn("testClientSecret");

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertFalse(tokenValidationForEventCalendar.isAccessTokenValid("exceptionAccessToken"));
    }

    @Test
    public void testRefreshAccessToken_Error() {
        when(oauth2Config.getClientId()).thenReturn("testClientId");
        when(oauth2Config.getClientSecret()).thenReturn("testClientSecret");

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        String refreshToken = "invalidRefreshToken";
        assertThrows(RuntimeException.class, () -> tokenValidationForEventCalendar.refreshAccessToken(refreshToken));
    }

    @Test
    public void testRefreshAccessToken_Exception() {
        when(oauth2Config.getClientId()).thenReturn("testClientId");
        when(oauth2Config.getClientSecret()).thenReturn("testClientSecret");

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        String refreshToken = "invalidRefreshToken";
        assertThrows(RuntimeException.class, () -> tokenValidationForEventCalendar.refreshAccessToken(refreshToken));
    }
}
