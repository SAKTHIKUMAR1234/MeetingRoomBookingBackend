package com.divum.MeetingRoomBlocker.Service;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Timer;

@Service
public interface MeetingEntityService {

    ResponseEntity<?> viewslots(long id, LocalDate date);

    public ResponseEntity<?> findByYear(int year);
}
