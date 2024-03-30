package com.divum.MeetingRoomBlocker.API.NotificationAPI;


import com.divum.MeetingRoomBlocker.DTO.FeedBackDto.FeedBackDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Date;

@RequestMapping("${Feedback_Api}")
public interface FeedBackAPI {

    @PostMapping("${SaveFeedback}")
    public ResponseEntity<?> Savefeedback(@RequestBody FeedBackDTO feedBackDTO);

    @GetMapping("${Last4Feedback}")
    public ResponseEntity<?> GetAllFeedBack();

    @GetMapping("${FeedbackDate}")
    public  ResponseEntity<?> getFeedBackOfDate(@RequestParam("date") Date date);

}
