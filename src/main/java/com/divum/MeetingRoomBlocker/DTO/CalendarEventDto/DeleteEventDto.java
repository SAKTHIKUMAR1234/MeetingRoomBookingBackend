package com.divum.MeetingRoomBlocker.DTO.CalendarEventDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteEventDto {
    private String organizerEmail;
}
