package com.divum.MeetingRoomBlocker.Scheduler;

import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import lombok.RequiredArgsConstructor;
import com.divum.MeetingRoomBlocker.Service.MailServices.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class FeedBackScheduler {

    @Autowired
    private MailService mailService;
    @Autowired
    private TaskTrigger taskTrigger;
    @Autowired
    private MeetingEntityRepository meetingEntityRepository;
    @Scheduled(fixedDelay = 1000*3*60)
    public void scheduleTask() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Timestamp startTime = Timestamp.valueOf(now);
        Timestamp endTime = Timestamp.valueOf(now.plusMinutes(30));
    }


}
