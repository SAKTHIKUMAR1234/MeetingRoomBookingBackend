package com.divum.MeetingRoomBlocker.Controller.NotificationController;

import com.divum.MeetingRoomBlocker.API.NotificationAPI.FeedBackAPI;

import com.divum.MeetingRoomBlocker.DTO.FeedBackDto.FeedBackDTO;
import com.divum.MeetingRoomBlocker.Service.FeedBackServices;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@Controller
@CrossOrigin
@RequiredArgsConstructor
public class FeedBackController implements FeedBackAPI {

    private final FeedBackServices feedBackServices;

    @Override
    public ResponseEntity<?> Savefeedback(@RequestBody FeedBackDTO feedBackDTO){
        return feedBackServices.SaveFeedBackOfUser(feedBackDTO);
    }

    @Override
    public ResponseEntity<?> GetAllFeedBack(){
        return feedBackServices.GetLastFourofFeedback();
    }

    @Override
    public  ResponseEntity<?> getFeedBackOfDate(@RequestParam("date") Date date){
        return feedBackServices.getFeedBackOnDate(date);
    }

}
