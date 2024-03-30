package com.divum.MeetingRoomBlocker.Service.Implementation.FeedBackImplementation;

import com.divum.MeetingRoomBlocker.DTO.FeedBackDto.FeedBackDTO;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.FeedbackEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Exception.CustomExceptionHandler;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.FeedbackEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.MeetingEntityRepository;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.FeedBackServices;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServicesImplementation implements FeedBackServices {

    private final FeedbackEntityRepository feedbackEntityRepository;

    private final MeetingEntityRepository meetingEntityRepository;

    private final UserEntityRepository userEntityRepository;


    private final CustomExceptionHandler customExceptionHandler;

    @Override
    public ResponseEntity<?> SaveFeedBackOfUser(FeedBackDTO feedBackDTO){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        MeetingEntity meeting=meetingEntityRepository.findById(feedBackDTO.getMeetingId()).orElseThrow(()->new DataNotFoundException(Constants.MEETING_NOT_FOUND));
        if (feedBackDTO != null && meeting.getStatus().equals(MeetingStatusEntity.COMPLETED) && email.equals(meeting.getHost().getEmail())) {
            FeedbackEntity newFeedbackEntity = FeedbackEntity.
                    builder().
                    feedback(feedBackDTO.getFeedback()).
                    submittedAt(Timestamp.valueOf((LocalDateTime.now()))).
                    rating(feedBackDTO.getRating()).
                    meetingEntity(meeting)
                    .userEntity(userEntityRepository.findByEmail(email).get())
                    .build();
            feedbackEntityRepository.save(newFeedbackEntity);
            ResponseDTO responseDTO = ResponseDTO.builder().
                    httpStatus(HttpStatus.OK.getReasonPhrase())
                    .data(Constants.FEEDBACK_ADDED).
                    message(Constants.FEEDBACK_ADDED).
                    build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            return customExceptionHandler.HandleDataNotFoundException(new DataNotFoundException(Constants.DATA_NOT_FOUND));
        }
    }

    @Override
    public ResponseEntity<?> getFeedBackOnDate(Date timestamp) {
        List<FeedbackEntity> feedbackEntityList = feedbackEntityRepository.findBySubmittedDate(timestamp);
        for (FeedbackEntity feedbacks : feedbackEntityList) {
            feedbacks.setSeen(true);
            feedbacks.setId(feedbacks.getId());
            feedbackEntityRepository.save(feedbacks);
        }
        List<FeedBackDTO> GetFeedBackList=new ArrayList<>();
        feedbackEntityList.stream().map((feedbackEntity -> FeedBackDTO.builder().
                    Feedback(feedbackEntity.getFeedback()).
                    hostName(feedbackEntity.getUserEntity().getName()).
                    timestamp(feedbackEntity.getSubmittedAt()).
                    Rating(feedbackEntity.getRating()).
                    roomName(feedbackEntity.getMeetingEntity().getRoomEntity().getName()).
                            build())).

                    forEach(GetFeedBackList::add);
        ResponseDTO responseDTO = ResponseDTO.builder().httpStatus(HttpStatus.OK.getReasonPhrase()).
                    data(GetFeedBackList).
                    message(Constants.FEEDBACK_FETCHED)
                    .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> GetLastFourofFeedback() {
        List<FeedbackEntity> FeedBack = feedbackEntityRepository.findTop4ByOrderByModifiedAtDesc();
        List<FeedBackDTO> FeedofLastUser = new ArrayList<>();
        FeedBack.stream()
                .filter(feedback->feedback.isSeen()==false)
                .map(feedbackEntity ->
                        FeedBackDTO.builder()
                                .Feedback(feedbackEntity.getFeedback())
                                .hostName(feedbackEntity.getMeetingEntity().getHost().getName())
                                .roomName(feedbackEntity.getMeetingEntity().getRoomEntity().getName())
                                .build())
                .forEach(FeedofLastUser::add);
        ResponseDTO responseDTO = ResponseDTO.builder().
                httpStatus(HttpStatus.OK.getReasonPhrase()).
                message(Constants.FEEDBACK_FETCHED).
                data(FeedofLastUser).
                build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }




}
