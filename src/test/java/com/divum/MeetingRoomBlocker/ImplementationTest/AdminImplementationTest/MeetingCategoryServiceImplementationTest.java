package com.divum.MeetingRoomBlocker.ImplementationTest.AdminImplementationTest;

import com.divum.MeetingRoomBlocker.DTO.CategoryDTO.MeetingCategoryDTO;
import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Entity.MeetingCategoryEntity;
import com.divum.MeetingRoomBlocker.Exception.DuplicateItemError;
import com.divum.MeetingRoomBlocker.Service.Implementation.AdminImplementation.MeetingCategoryServicesImplementation;
import com.divum.MeetingRoomBlocker.Repository.MeetingCategoryEntityRepository;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MeetingCategoryServiceImplementationTest {

    @Mock
    private MeetingCategoryEntityRepository meetingCategoryEntityRepository;

    @InjectMocks
    private MeetingCategoryServicesImplementation meetingCategoryServicesImplementation;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCategoryTest(){
        MeetingCategoryDTO meetingCategoryDTO = new MeetingCategoryDTO();
        meetingCategoryDTO.setMeetingCategoryName("Client Meeting");

        MeetingCategoryEntity meetingCategory = new MeetingCategoryEntity();
        meetingCategory.setCategoryName(meetingCategoryDTO.getMeetingCategoryName());

        when(meetingCategoryEntityRepository.findByCategoryName(meetingCategory.getCategoryName())).thenReturn(Optional.empty());
        when(meetingCategoryEntityRepository.save(meetingCategory)).thenReturn(meetingCategory);

        ResponseEntity<?> response = meetingCategoryServicesImplementation.addCategory(meetingCategoryDTO);
        assertNotNull(response);

        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        assertEquals(HttpStatus.CREATED.getReasonPhrase(),responseDTO.getHttpStatus());
        assertEquals(Constants.CATEGORY_CREATED,responseDTO.getMessage());
        assertNotNull(responseDTO);

    }

    @Test
    void CategoryAlreadyPresent(){
        MeetingCategoryDTO meetingCategoryDTO = new MeetingCategoryDTO();
        meetingCategoryDTO.setMeetingCategoryName("Client Meeting");

        MeetingCategoryEntity meetingCategory = new MeetingCategoryEntity();
        meetingCategory.setCategoryName(meetingCategoryDTO.getMeetingCategoryName());

        when(meetingCategoryEntityRepository.findByCategoryName(meetingCategory.getCategoryName())).thenReturn(Optional.of(meetingCategory));
        assertThrows(DuplicateItemError.class, ()-> meetingCategoryServicesImplementation.addCategory(meetingCategoryDTO));
    }
}
