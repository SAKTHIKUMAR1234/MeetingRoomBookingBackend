package com.divum.MeetingRoomBlocker.API.AdminAPI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("${Facility_Api}")
public interface FacilitiesAPI {

    @PostMapping("${AddApi}")
    public ResponseEntity<?> addFacility(@RequestParam("facilityIcon")MultipartFile multipartFile,@RequestParam("facilityName") String facilityName);

    @GetMapping("${FacilityDisplay}")
    public ResponseEntity<?> getAllFacility();
}
