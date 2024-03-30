package com.divum.MeetingRoomBlocker.UtilTest;

import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.UpdateEventDto;
import com.divum.MeetingRoomBlocker.Util.UpdateEventInCalendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateEventInCalendarTest {

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateExistingEvent_AddAttendees() {
        Event existingEvent = new Event();
        List<EventAttendee> existingAttendees = new ArrayList<>();
        existingAttendees.add(new EventAttendee().setEmail("existing1@example.com"));
        existingEvent.setAttendees(existingAttendees);

        UpdateEventDto updateEventDto = new UpdateEventDto();
        List<String> attendeesToAdd = new ArrayList<>();
        attendeesToAdd.add("new1@example.com");
        attendeesToAdd.add("new2@example.com");
        updateEventDto.setAttendeesToAdd(attendeesToAdd);

        Event updatedEvent = UpdateEventInCalendar.updateExistingEvent(existingEvent, updateEventDto);

        assertEquals(3, updatedEvent.getAttendees().size()); // 1 existing + 2 new
        assertEquals("existing1@example.com", updatedEvent.getAttendees().get(0).getEmail());
        assertEquals("new1@example.com", updatedEvent.getAttendees().get(1).getEmail());
        assertEquals("new2@example.com", updatedEvent.getAttendees().get(2).getEmail());
    }

    @Test
    public void testUpdateExistingEvent_RemoveAttendees() {
        Event existingEvent = new Event();
        List<EventAttendee> existingAttendees = new ArrayList<>();
        existingAttendees.add(new EventAttendee().setEmail("existing1@example.com"));
        existingAttendees.add(new EventAttendee().setEmail("existing2@example.com"));
        existingEvent.setAttendees(existingAttendees);

        UpdateEventDto updateEventDto = new UpdateEventDto();
        List<String> attendeesToRemove = new ArrayList<>();
        attendeesToRemove.add("existing1@example.com");
        updateEventDto.setAttendeesToRemove(attendeesToRemove);
        Event updatedEvent = UpdateEventInCalendar.updateExistingEvent(existingEvent, updateEventDto);

        assertEquals(1, updatedEvent.getAttendees().size()); // Only 1 attendee remaining
        assertEquals("existing2@example.com", updatedEvent.getAttendees().get(0).getEmail());
    }

    @Test
    public void testUpdateExistingEvent_NoChanges() {
        Event existingEvent = new Event();
        List<EventAttendee> existingAttendees = new ArrayList<>();
        existingAttendees.add(new EventAttendee().setEmail("existing1@example.com"));
        existingEvent.setAttendees(existingAttendees);
        UpdateEventDto updateEventDto = new UpdateEventDto();

        Event updatedEvent = UpdateEventInCalendar.updateExistingEvent(existingEvent, updateEventDto);

        assertEquals(existingEvent, updatedEvent);
    }
}
