package com.divum.MeetingRoomBlocker.Service.Implementation.MailImplementation;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.Enum.RoleEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Exception.CustomExceptionHandler;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.MailServices.MailService;

import lombok.RequiredArgsConstructor;
import com.divum.MeetingRoomBlocker.Constants.Constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailServicesImplementation implements MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String From;

    private final MeetingEntityRepository meetingEntityRepository;
    private final RoomEntityRepository roomEntityRepository;
    private final CustomExceptionHandler customExceptionHandler;
    private final UserEntityRepository userEntityRepository;

    @Override
    public ResponseEntity<?> SendMailToMeetingAttenders(long meetingId) {

        MeetingEntity getMeeting = meetingEntityRepository.findById(meetingId).orElseThrow(()->new DataNotFoundException(Constants.MEETING_NOT_FOUND));
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<RoomEntity> room = roomEntityRepository.findById(getMeeting.getRoomEntity().getId());
        LocalDateTime startTime = getMeeting.getStartTime().toLocalDateTime();
        LocalDateTime endTime = getMeeting.getEndTime().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedStartTime = startTime.format(formatter);
        String formattedEndTime = endTime.format(formatter);
        UserEntity user= userEntityRepository.findByEmail(email).orElseThrow(()->new DataNotFoundException(Constants.USER_NOT_FOUND));
        if (getMeeting != null && ((getMeeting.getStatus().equals(MeetingStatusEntity.ACCEPTED)) || (getMeeting.getStatus().equals(MeetingStatusEntity.REJECTED))&&user.getRole().equals(RoleEntity.ADMIN) )){
            List<UserEntity> GetAttenders = meetingEntityRepository.findGuestListByMeetingId(meetingId);
            String AcceptText = "Dear Sir/Madam, \nI am writing to inform you that you have been scheduled to a meeting. The details of meetings are as follows: " +
                                "\nMeeting Room: "+room.get().getName()+" \nStart Time: "+formattedStartTime+" \nEnd Time: "+formattedEndTime+ "\nPlease ensure that you arrive promptly at the scheduled time. If you have any queries or need further assistance, feel free to contact us.\n" +
                                "Thank you for using our meeting room facilities.\n" +
                                "Best regards,\n" +
                                "Admin\n" +
                                "Divum";
        String RejectText = "Dear Sir/Madam, \nI regret to inform you that your meeting which is scheduled on "+startTime+" has been rejected due to the reason "+getMeeting.getReason()+". We apologize for any inconvenience this may cause. The details of your rejected meeting are as follows: " +
                            "\nMeeting Room: "+room.get().getName()+" \nStart Time: "+formattedStartTime+" \nEnd Time: "+formattedEndTime+"\nIf you have any queries or need further clarification, please don't hesitate to reach out to us.\n" +
                            "Thank you for your understanding.\n" +
                            "Best regards,\n" +
                            "Admin\n" +
                            "Divum";
            String text= getMeeting.getStatus().equals(MeetingStatusEntity.ACCEPTED)?AcceptText:RejectText;
            for (UserEntity Attender : GetAttenders) {
                if (Attender.getId() != getMeeting.getHost().getId()) {
                    String setFrom=From;
                    String setTo=Attender.getEmail();
                    String setSubject= Constants.SUBJECT;
                    SendMail(setFrom,setTo,text,setSubject);
                }
            }
            text=getMeeting.getStatus().equals(MeetingStatusEntity.ACCEPTED)? AcceptText:RejectText;
            String setTo=getMeeting.getHost().getEmail();
            String setSubject=(Constants.SUBJECT);
            String setText=text;
            SendMail(From,setTo,setText,setSubject);
            String message=getMeeting.getStatus().equals(MeetingStatusEntity.ACCEPTED)? Constants.ACCEPTED_EMAIL: Constants.ADMIN_CANCELLATION_EMAIL;
            ResponseDTO responseDTO= ResponseDTO.builder().
                    httpStatus(HttpStatus.OK.getReasonPhrase()).
                    message(message).
                    data(message).
                    build();
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }else if(getMeeting != null && (getMeeting.getStatus().equals(MeetingStatusEntity.CANCELLED))){
            List<UserEntity> GetAttenders = meetingEntityRepository.findGuestListByMeetingId(meetingId);
            String setText= "Dear Sir/Madam, \nI regret to inform you that your meeting which is scheduled on "+startTime+" has been rejected by your meeting host. We apologize for any inconvenience this may cause. The details of your rejected meeting are as follows: " +
                    "\nMeeting Room: "+room.get().getName()+" \nStart Time: "+formattedStartTime+" \nEnd Time: "+formattedEndTime+"\nIf you have any queries or need further clarification, please don't hesitate to reach out to us.\n" +
                    "Thank you for your understanding.\n";
            for (UserEntity Attender : GetAttenders) {
                if (Attender.getId() != getMeeting.getHost().getId()) {
                    String setFrom=From;
                    String setTo=Attender.getEmail();
                    String setSubject= Constants.SUBJECT;
                    SendMail(setFrom,setTo,setText,setSubject);
                }
            }
            ResponseDTO responseDTO= ResponseDTO.builder().
                    httpStatus(HttpStatus.OK.getReasonPhrase()).
                    message(Constants.CANCELLATION_EMAIL).
                    data(Constants.CANCELLATION_EMAIL).
                    build();

             return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        } else {
            return customExceptionHandler.HandleDataNotFoundException(new DataNotFoundException("Data Not found in Meeting Entity"));
        }
    }

    @Override
    public ResponseEntity<?> MeetingCancel(long meetingid,String Reason) {
        Optional<MeetingEntity> getMeeting = meetingEntityRepository.findById(meetingid);
        if (getMeeting.isPresent() && getMeeting.get().getStatus().equals(MeetingStatusEntity.REJECTED)) {
            Optional<UserEntity> userEntityOptional = userEntityRepository.findById(getMeeting.get().getHost().getId());
            Optional<RoomEntity> room = roomEntityRepository.findById(getMeeting.get().getRoomEntity().getId());
            String SetTo = getMeeting.get().getHost().getEmail();
            LocalDateTime startTime = getMeeting.get().getStartTime().toLocalDateTime();
            LocalDateTime endTime = getMeeting.get().getEndTime().toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedStartTime = startTime.format(formatter);
            String formattedEndTime = endTime.format(formatter);
            String SetText = "Dear "+userEntityOptional.get().getName()+", \nI regret to inform you that your meeting room booking request has been rejected due to the reason "+getMeeting.get().getReason()+". We apologize for any inconvenience this may cause. The details of your rejected booking request are as follows: " +
                    "\nMeeting Room: "+room.get().getName()+" \nStart Time: "+formattedStartTime+" \nEnd Time: "+formattedEndTime+"\nIf you have any queries or need further clarification, please don't hesitate to reach out to us.\n" +
                    "Thank you for your understanding.\n" +
                    "Best regards,\n" + "Admin\n" +
                    "Divum";
            String SetSubject =Constants.SUBJECT;
            SendMail(From,SetTo,SetText,SetSubject);
            ResponseDTO responseDTO = ResponseDTO.builder().
                    data(Constants.ADMIN_CANCELLATION_EMAIL).
                    message(Constants.ADMIN_CANCELLATION_EMAIL).
                    httpStatus(HttpStatus.OK.getReasonPhrase()).build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
        else {
            return customExceptionHandler.HandleDataNotFoundException(new DataNotFoundException("Data Not found in Meeting Entity"));
        }
    }

    @Async
    public void SendMail(String Setfrom,String SetTo,String setText,String Subject)
    {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(Setfrom);
        simpleMailMessage.setTo(SetTo);
        simpleMailMessage.setSubject(Subject);
        simpleMailMessage.setText(setText);
        javaMailSender.send(simpleMailMessage);
    }
}


