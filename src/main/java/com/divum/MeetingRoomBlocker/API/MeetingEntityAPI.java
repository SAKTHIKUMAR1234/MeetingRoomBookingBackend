package com.divum.MeetingRoomBlocker.API;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("${Booking_Api}")
public interface MeetingEntityAPI {

    @GetMapping("${ViewSlots}")
    public ResponseEntity<?> viewallslots(@PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date);

    @GetMapping("${Calender}")
    public ResponseEntity<?> yearandmonthmeetings(@PathVariable("year")int year);

}
