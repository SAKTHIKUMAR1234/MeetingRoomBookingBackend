package com.divum.MeetingRoomBlocker.ImplementationTest.CalendarImplementation;

import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.CreateEventDto;
import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.DeleteEventDto;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.GoogleCloudApiService.GoogleCloudService;
import com.divum.MeetingRoomBlocker.Service.Implementation.CalendarImplementation.CalendarEventImplementation;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import com.divum.MeetingRoomBlocker.Util.CreateEventInCalendar;
import com.divum.MeetingRoomBlocker.Util.TokenValidationForEventCalendar;
import com.google.api.services.calendar.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class CalendarEventImplementationTest {


    @Mock
    private UserEntityRepository userEntityRepository;
    @Mock
    private TokenValidationForEventCalendar tokenValidationForEventCalendar;
    @Mock
    private CreateEventInCalendar createEventInCalendar;
    @Mock
    private GoogleCloudService googleCloudService;
    @Mock
    private MeetingEntityRepository meetingEntityRepository;
    @InjectMocks
    private CalendarEventImplementation calendarEventImplementation;
    private CreateEventDto createEventDto;
    private UserEntity organizerDetails;
    private MeetingEntity meeting;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        createEventDto = new CreateEventDto();
        createEventDto.setOrganizerEmail("user@divum.in");
        Date date=Date.valueOf("2024-03-19");
        Date enddate=Date.valueOf("2024-03-19");
        createEventDto.setStartTime(date);
        createEventDto.setEndTime(enddate);
        organizerDetails = new UserEntity();
        organizerDetails.setEmail("user@divum.in");
        organizerDetails.setName("user");
        organizerDetails.setAccessToken("accessToken");
        organizerDetails.setRefreshToken("RefreshToken");
        createEventDto.setMeetingId(1L);

        meeting =new MeetingEntity();
        meeting.setId(1L);
        meeting.setMeetingName("Mrb meeting 1");
        meeting.setHost(organizerDetails);
        meeting.setStatus(MeetingStatusEntity.PENDING);

        meeting.setStartTime(Timestamp.valueOf(LocalDateTime.parse("2024-02-26T10:00:00")));
        meeting.setEndTime(Timestamp.valueOf(LocalDateTime.parse("2024-02-26T12:00:00")));



    }

    @Test
    public void testCalendarCreateEventtest() {

        Event createdEvent = new Event();
        createdEvent.setSummary("Google calendar");
        createdEvent.setId("eventId");

        when(userEntityRepository.findByEmail("user@divum.in")).thenReturn(Optional.of(organizerDetails));
        when(tokenValidationForEventCalendar.isAccessTokenValid(organizerDetails.getAccessToken())).thenReturn(false);
        when(tokenValidationForEventCalendar.refreshAccessToken(organizerDetails.getRefreshToken())).thenReturn("new_access_token");
        when(createEventInCalendar.createEventObject(createEventDto)).thenReturn(createdEvent);
        when(meetingEntityRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(googleCloudService.eventCreation("new_access_token", createdEvent, "user@divum.in")).thenReturn(createdEvent);
        ResponseEntity<?> responseEntity = calendarEventImplementation.calendarCreateEvent(createEventDto);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Constants.CALENDER_CREATED + createdEvent.getId(), responseEntity.getBody());
    }

    @Test
    public void testcalendardelete(){
        when(userEntityRepository.findByEmail("user@divum.in")).thenReturn(Optional.of(organizerDetails));
        when(tokenValidationForEventCalendar.isAccessTokenValid(organizerDetails.getAccessToken())).thenReturn(false);
        when(tokenValidationForEventCalendar.refreshAccessToken(organizerDetails.getRefreshToken())).thenReturn("new_access_token");
        String eventId = "eventId";
        doNothing().when(googleCloudService).deleteEventinCalendar("access token", eventId, organizerDetails.getEmail());
        DeleteEventDto deleteEventDto=new DeleteEventDto();
        deleteEventDto.setOrganizerEmail("user@divum.in");
        ResponseEntity<?> response = calendarEventImplementation.calendarDeleteEvent(eventId,deleteEventDto );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.CALENDER_DELETED, response.getBody());

    }
    @Test
    public void testCalendarCreateEvent_UserNotFound(){
        String organizerEmail = "organizer@example.com";
        Event createdEvent = new Event();
        createdEvent.setSummary("Google calendar");
        createdEvent.setId("eventId");
        when(userEntityRepository.findByEmail(organizerEmail)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () ->
                calendarEventImplementation.calendarCreateEvent(createEventDto)
        );
    }

    @Test
    public void testCalendarDeleteEvent_UserNotFound() {
        String organizerEmail = "organizer@example.com";
        when(userEntityRepository.findByEmail(organizerEmail)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () ->
                calendarEventImplementation.calendarDeleteEvent("eventId", new DeleteEventDto(organizerEmail))
        );
    }

}