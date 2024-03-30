package com.divum.MeetingRoomBlocker.DTO.RoomDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomEntityDTO {


    private String name;
    private Integer maxCapacity;
    private Integer minCapacity;
    private List<String> facilitiesEntityList;
    private MultipartFile roomImage;

}
