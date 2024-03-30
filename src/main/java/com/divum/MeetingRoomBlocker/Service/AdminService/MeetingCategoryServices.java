package com.divum.MeetingRoomBlocker.Service.AdminService;


import com.divum.MeetingRoomBlocker.DTO.CategoryDTO.MeetingCategoryDTO;
import org.springframework.http.ResponseEntity;

public interface MeetingCategoryServices {

    ResponseEntity<?> addCategory(MeetingCategoryDTO meetingCategoryDTO);

}
