package com.divum.MeetingRoomBlocker.DTO.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class InternalUserDTO implements Serializable {

   private  String name;
   private  String email;
}