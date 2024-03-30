package com.divum.MeetingRoomBlocker.Controller;

import com.divum.MeetingRoomBlocker.API.MeetingEntityAPI;
import com.divum.MeetingRoomBlocker.Service.MeetingEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RequiredArgsConstructor
@RestController
@CrossOrigin
public class MeetingEntityController implements MeetingEntityAPI {

    private final MeetingEntityService meetingEntityService;

    @Override
    public ResponseEntity<?> viewallslots(@PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return meetingEntityService.viewslots(id, date);
    }

    @Override
    public ResponseEntity<?> yearandmonthmeetings(@PathVariable("year")int year){
        return meetingEntityService.findByYear(year);
    }
}
