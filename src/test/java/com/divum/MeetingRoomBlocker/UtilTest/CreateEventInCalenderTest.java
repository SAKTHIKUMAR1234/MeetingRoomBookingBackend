package com.divum.MeetingRoomBlocker.UtilTest;

import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.CreateEventDto;
import com.divum.MeetingRoomBlocker.Util.CreateEventInCalendar;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateEventInCalenderTest {

    @InjectMocks
    private CreateEventInCalendar createEventInCalendar;

    @BeforeEach
    public  void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateEventObject() {
        CreateEventDto createEventDto = new CreateEventDto();
        createEventDto.setSummary("Test Event");
        createEventDto.setStartTime(Date.valueOf(LocalDate.now()));
        createEventDto.setEndTime(Date.valueOf(LocalDate.now()));
        createEventDto.setDescription("This is a test event");
        createEventDto.setOrganizerEmail("organizer@example.com");
        List<String> attendees = new ArrayList<>();
        attendees.add("attendee1@example.com");
        attendees.add("attendee2@example.com");
        createEventDto.setAttendees(attendees);
        Event event = createEventInCalendar.createEventObject(createEventDto);
        assertEquals(createEventDto.getSummary(), event.getSummary());
        assertEquals(createEventDto.getDescription(), event.getDescription());
        assertEquals(createEventDto.getOrganizerEmail(), event.getAttendees().get(0).getEmail());
        EventDateTime start = event.getStart();
        EventDateTime end = event.getEnd();
        assertEquals(new DateTime(createEventDto.getStartTime()), start.getDateTime());
        assertEquals(new DateTime(createEventDto.getEndTime()), end.getDateTime());
        for (int i = 0; i < createEventDto.getAttendees().size(); i++) {
            assertEquals(createEventDto.getAttendees().get(i), event.getAttendees().get(i + 1).getEmail());
        }
    }

}
