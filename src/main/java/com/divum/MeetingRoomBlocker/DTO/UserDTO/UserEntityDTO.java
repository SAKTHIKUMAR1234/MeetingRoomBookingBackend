package com.divum.MeetingRoomBlocker.DTO.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityDTO {

    private Long id;

    private String name;

    private String email;

}
