package com.divum.MeetingRoomBlocker.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO implements Serializable {

    private String httpStatus;
    private String message;
    private Object data;

}
