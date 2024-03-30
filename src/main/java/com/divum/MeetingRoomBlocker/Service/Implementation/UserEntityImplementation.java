package com.divum.MeetingRoomBlocker.Service.Implementation;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.UserDTO.UserEntityDTO;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.UserEntityService;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserEntityImplementation implements UserEntityService {

    private final UserEntityRepository userRepository;

    @Override
    public ResponseEntity<?> getUserByEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new DataNotFoundException(Constants.USER_NOT_FOUND);
        }
            List<UserEntityDTO> userEntityDTOS = userOptional.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .message(Constants.USER_DETAILS_FETCHED)
                    .data(userEntityDTOS)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    private UserEntityDTO convertToDTO(UserEntity userEntity) {
        return UserEntityDTO.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }

}
