package com.divum.MeetingRoomBlocker.API.AdminAPI;


import com.divum.MeetingRoomBlocker.DTO.CategoryDTO.MeetingCategoryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${MeetingCategoryAdmin_Api}")
public interface MeetingCategoryAPI {

    @PostMapping("${AddApi}")
    ResponseEntity<?> createMeetingCategory(@RequestBody MeetingCategoryDTO meetingCategoryDTO);
}
