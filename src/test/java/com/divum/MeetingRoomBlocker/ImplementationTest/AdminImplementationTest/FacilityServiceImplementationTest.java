package com.divum.MeetingRoomBlocker.ImplementationTest.AdminImplementationTest;

import com.divum.MeetingRoomBlocker.DTO.FacilityDTO.FacilityDTO;
import com.divum.MeetingRoomBlocker.DTO.RoomDTO.ImageUploadResponseDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.FacilitiesEntity;
import com.divum.MeetingRoomBlocker.Exception.CloudStorageException;
import com.divum.MeetingRoomBlocker.Exception.DuplicateItemError;
import com.divum.MeetingRoomBlocker.Service.Implementation.AdminImplementation.FacilitiesServicesImplementation;
import com.divum.MeetingRoomBlocker.Repository.FacilitiesEntityRepository;
import com.divum.MeetingRoomBlocker.Service.AwsServices;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FacilityServiceImplementationTest {

    @Mock
    private FacilitiesEntityRepository facilitiesEntityRepository;
    @Mock
    private AwsServices awsServices;
    @InjectMocks
    private FacilitiesServicesImplementation facilitiesServicesImplementation;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFacilityTest(){
        String facilityName = "TV";
        MultipartFile multipartFile = mock(MultipartFile.class);

        ImageUploadResponseDTO image = new ImageUploadResponseDTO();

        FacilitiesEntity facilities = new FacilitiesEntity();
        facilities.setFacilityName(facilityName);

        when(awsServices.uploadImage(multipartFile)).thenReturn(image);
        when(facilitiesEntityRepository.findByFacilityName(facilityName)).thenReturn(Optional.empty());
        when(facilitiesEntityRepository.save(any())).thenReturn(facilities);

        ResponseEntity<?> responseEntity = facilitiesServicesImplementation.addFacility(multipartFile, facilityName);
        assertNotNull(responseEntity);

        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.CREATED.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.FACILITY_CREATED,responseDTO.getMessage());

    }

    @Test
    void FacilityEmptyTest(){
        MultipartFile multipartFile = mock(MultipartFile.class);
        String facilityName = "TV";

        FacilitiesEntity facilitiesEntity = new FacilitiesEntity();
        facilitiesEntity.setFacilityName(facilityName);

        when(facilitiesEntityRepository.findByFacilityName(facilityName)).thenReturn(Optional.of(facilitiesEntity));
        assertThrows(DuplicateItemError.class, ()->facilitiesServicesImplementation.addFacility(multipartFile,facilityName));

    }

    @Test
    void ImageUploadErrorTest(){
        MultipartFile multipartFile = mock(MultipartFile.class);
        String facilityName = "TV";

        ImageUploadResponseDTO image = new ImageUploadResponseDTO();
        image.setError(true);
        image.setErrorString("Error Message");

        when(awsServices.uploadImage(multipartFile)).thenReturn(image);
        assertThrows(CloudStorageException.class, ()->facilitiesServicesImplementation.addFacility(multipartFile, facilityName));
    }

    @Test
    void getAllFacilitiesTest(){
        List<FacilityDTO> facilitiesEntities = new ArrayList<>();
        when(facilitiesEntityRepository.findAllFacilities()).thenReturn(facilitiesEntities);
        ResponseEntity<?> responseEntity = facilitiesServicesImplementation.getAllFacilities();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.FACILITY_FETCHED,responseDTO.getMessage());
    }

}
