package com.divum.MeetingRoomBlocker.DTO.MeetingDTO;

import com.divum.MeetingRoomBlocker.DTO.UserDTO.InternalUserDTO;
import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingActivityCompletedDTO {
    private Long id;
    private String meetingName;
    private String  meetingCategory;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private MeetingStatusEntity status;
    private String roomName;
    private String hostName;
    private List<InternalUserDTO> guestList;
    private String reason;
    private  String description;
    private String hostEmail;
    private boolean feedback;
}
