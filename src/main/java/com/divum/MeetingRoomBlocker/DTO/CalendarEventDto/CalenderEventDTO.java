package com.divum.MeetingRoomBlocker.DTO.CalendarEventDto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CalenderEventDTO {
    @JsonIgnore
    private Long id;
    private String start;
    private String end;
    private String title;
    private String hostName;
    private String roomName;

}
