package com.divum.MeetingRoomBlocker.DTO.MeetingDTO;

import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import java.util.List;
@Data
public class MeetingEntityRequestDTO {
    @NotBlank(message = "Meeting Name is required")
    private String meetingName;
    @NotBlank(message = "Meeting Category is required")
    private String  meetingCategory;
    @Size(min = 1, message = "At least one user must be specified")
    private List<String> userEntityList;
    @NotBlank(message ="Description is required")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    @NotBlank(message = "Start Time is required")
    private String startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    @NotBlank(message = "End Time is required")
    private String endTime;
    @JsonIgnore
    private MeetingStatusEntity status;
    @NotBlank(message ="Room id is required")
    private Long roomEntityId;
    private String reason;

}
