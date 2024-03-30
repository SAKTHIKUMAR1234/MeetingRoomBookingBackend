package com.divum.MeetingRoomBlocker.Controller.AdminController;

import com.divum.MeetingRoomBlocker.API.AdminAPI.MeetingCategoryAPI;
import com.divum.MeetingRoomBlocker.DTO.CategoryDTO.MeetingCategoryDTO;
import com.divum.MeetingRoomBlocker.Service.AdminService.MeetingCategoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MeetingCategoryController implements MeetingCategoryAPI {

    private final MeetingCategoryServices meetingCategoryServices;

    @Override
    public ResponseEntity<?> createMeetingCategory(MeetingCategoryDTO meetingCategoryDTO) {
        return meetingCategoryServices.addCategory(meetingCategoryDTO);
    }
}
