package com.divum.MeetingRoomBlocker.Service.MailServices;


import org.springframework.http.ResponseEntity;

public interface MailService {

    ResponseEntity<?> SendMailToMeetingAttenders(long meetingid);

    ResponseEntity<?> MeetingCancel(long meetingid, String Reason);

}
