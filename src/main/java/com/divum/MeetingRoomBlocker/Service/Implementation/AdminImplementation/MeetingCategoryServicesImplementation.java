package com.divum.MeetingRoomBlocker.Service.Implementation.AdminImplementation;

import com.divum.MeetingRoomBlocker.DTO.CategoryDTO.MeetingCategoryDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.MeetingCategoryEntity;
import com.divum.MeetingRoomBlocker.Exception.DuplicateItemError;
import com.divum.MeetingRoomBlocker.Repository.MeetingCategoryEntityRepository;
import com.divum.MeetingRoomBlocker.Service.AdminService.MeetingCategoryServices;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MeetingCategoryServicesImplementation implements MeetingCategoryServices {

    private final MeetingCategoryEntityRepository meetingCategoryEntityRepository;

    @Override
    public ResponseEntity<?> addCategory(MeetingCategoryDTO meetingCategoryDTO) {
        Optional<MeetingCategoryEntity> meetingCategoryEntityOptional=meetingCategoryEntityRepository.findByCategoryName(meetingCategoryDTO.getMeetingCategoryName());

        if(meetingCategoryEntityOptional.isPresent()){
            throw new DuplicateItemError(Constants.CATEGORY_ERROR);
        }

        MeetingCategoryEntity meetingCategoryEntity = MeetingCategoryEntity.builder()
                .categoryName(meetingCategoryDTO.getMeetingCategoryName())
                .build();
        meetingCategoryEntityRepository.save(meetingCategoryEntity);
        ResponseDTO responseDTO = ResponseDTO.builder()
                .httpStatus(HttpStatus.CREATED.getReasonPhrase())
                .message(Constants.CATEGORY_CREATED)
                .data(null)
                .build();
        return new ResponseEntity<>(responseDTO,HttpStatus.CREATED);
    }
}
