package com.divum.MeetingRoomBlocker.DTO.MeetingDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDeleteDTO {

    private List<Long> ids;
}
