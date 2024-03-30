package com.divum.MeetingRoomBlocker.Service.Implementation.UserImplementation;

import com.divum.MeetingRoomBlocker.DTO.UserDTO.InternalUserDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.UserService.UserEntityUserService;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
@RequiredArgsConstructor
public class UserEntityUserImplementation implements UserEntityUserService {

     private final UserEntityRepository userEntityRepository;

    @Override
    public ResponseEntity<?> getInternalUsers() {
       try {
           List<UserEntity> internalUsers = userEntityRepository.findAll();
           if(internalUsers.isEmpty()){
               throw new DataNotFoundException(Constants.USER_NOT_FOUND);
           }
           List<InternalUserDTO> internalUsersList = internalUsers.stream()
                   .map(this::covertToUserList)
                   .collect(Collectors.toList());
           ResponseDTO responseDTO = ResponseDTO.builder()
                   .httpStatus(HttpStatus.OK.getReasonPhrase())
                   .message(Constants.INTERNAL_ATTENDEES_FETCHED)
                   .data(internalUsersList)
                   .build();
           return new ResponseEntity<>(responseDTO,HttpStatus.OK);
       } catch (Exception e){
           ResponseDTO responseDTO = ResponseDTO.builder()
                   .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                   .message(Constants.INTERNAL_ERROR)
                   .build();
           return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    private InternalUserDTO covertToUserList(UserEntity userEntity) {
        return InternalUserDTO.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .build();

    }

}
