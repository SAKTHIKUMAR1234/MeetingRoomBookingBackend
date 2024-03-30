package com.divum.MeetingRoomBlocker.Service.CalendarService;

import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.CreateEventDto;
import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.DeleteEventDto;
import com.divum.MeetingRoomBlocker.DTO.CalendarEventDto.UpdateEventDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CalendarEventService {

   public ResponseEntity<?> calendarCreateEvent(CreateEventDto createEventDto);

   public ResponseEntity<?> calendarUpdateEvent(String eventId,UpdateEventDto updateEventDto);

   ResponseEntity<?> calendarDeleteEvent(String eventId, DeleteEventDto deleteEventDto);
}
