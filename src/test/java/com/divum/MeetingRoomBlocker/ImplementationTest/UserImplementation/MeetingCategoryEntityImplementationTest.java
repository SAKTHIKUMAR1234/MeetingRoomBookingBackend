package com.divum.MeetingRoomBlocker.ImplementationTest.UserImplementation;

import com.divum.MeetingRoomBlocker.DTO.ResponseDTO;
import com.divum.MeetingRoomBlocker.Repository.MeetingCategoryEntityRepository;
import com.divum.MeetingRoomBlocker.Service.Implementation.UserImplementation.MeetingCategoryEntityImplementation;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MeetingCategoryEntityImplementationTest {

    @Mock
    private MeetingCategoryEntityRepository meetingCategoryEntityRepository;
    @InjectMocks
    private MeetingCategoryEntityImplementation meetingCategoryEntityImplementation;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void getCategory_Sucess(){
        List<String> categories=new ArrayList<>();
         categories.add("Team Meeting");
         categories.add("Client Meeting");
        when(meetingCategoryEntityRepository.findAllCategories()).thenReturn(categories);

        ResponseEntity<?> responseEntity = meetingCategoryEntityImplementation.getCategories();
        assertNotNull(responseEntity);

        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getHttpStatus());
        assertEquals(Constants.CATEGORY_FETCHED,responseDTO.getMessage());
    }

    @Test
    public void getCategory_InternalServerError(){
        ResponseEntity<?> responseEntity = meetingCategoryEntityImplementation.getCategories();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }


}