package com.divum.MeetingRoomBlocker.Service.Implementation;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.RoomEntity;
import com.divum.MeetingRoomBlocker.Exception.DataNotFoundException;
import com.divum.MeetingRoomBlocker.Repository.RoomEntityRepository;
import com.divum.MeetingRoomBlocker.Service.RoomEntityService;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomEntityImplementation implements RoomEntityService {

    private final RoomEntityRepository roomEntityRepository;

    @Override
    public ResponseEntity<?> getAllRooms() {
        List<RoomEntity> rooms = roomEntityRepository.findAll(Sort.by("id").ascending());
        if(rooms.isEmpty()){
            throw new DataNotFoundException(Constants.ROOM_NOT_FOUND);
        }
        ResponseDTO responseDTO = ResponseDTO.builder()
                    .httpStatus(HttpStatus.OK.getReasonPhrase())
                    .message(Constants.ROOM_FETCHED)
                    .data(rooms)
                    .build();
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getRoomsById(Long id) {
        Optional<RoomEntity> room = roomEntityRepository.findById(id);
        if(room.isEmpty()){
            throw new DataNotFoundException(Constants.ROOM_NOT_FOUND);
        }
        ResponseDTO responseDTO = ResponseDTO.builder()
                        .httpStatus(HttpStatus.OK.getReasonPhrase())
                        .message(Constants.ROOM_FETCHED)
                        .data(room)
                        .build();
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
