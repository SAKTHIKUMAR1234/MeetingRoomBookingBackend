package com.divum.MeetingRoomBlocker.Service.Implementation.UserImplementation;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.MeetingCategoryEntityRepository;
import com.divum.MeetingRoomBlocker.Service.UserService.MeetingCategoryEntityService;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Builder
@RequiredArgsConstructor
public class MeetingCategoryEntityImplementation implements MeetingCategoryEntityService {

    private  final MeetingCategoryEntityRepository meetingCategoryEntityRepository;
    @Override
    public ResponseEntity<?> getCategories() {
        try{
        List<String> categories=meetingCategoryEntityRepository.findAllCategories();
        if(categories.isEmpty()){
            throw new DataNotFoundException(Constants.CATEGORY_NOT_FOUND);}
        ResponseDTO responseDTO = ResponseDTO.builder()
                .httpStatus(HttpStatus.OK.getReasonPhrase())
                .message(Constants.CATEGORY_FETCHED)
                .data(categories)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(Constants.INTERNAL_ERROR)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
