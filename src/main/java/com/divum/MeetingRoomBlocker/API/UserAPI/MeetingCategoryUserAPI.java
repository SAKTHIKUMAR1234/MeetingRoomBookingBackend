package com.divum.MeetingRoomBlocker.API.UserAPI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${MeetingCategory_Api}")
public interface MeetingCategoryUserAPI {

    @GetMapping("${CategoriesDisplay}")
    public ResponseEntity<?> getCategories();

}
