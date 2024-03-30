package com.divum.MeetingRoomBlocker.Service;

import com.divum.MeetingRoomBlocker.DTO.FeedBackDto.FeedBackDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public interface FeedBackServices {

    public ResponseEntity<?> SaveFeedBackOfUser(FeedBackDTO feedBackDTO);

    public ResponseEntity<?> getFeedBackOnDate(Date timestamp);

    public ResponseEntity<?> GetLastFourofFeedback();

}
