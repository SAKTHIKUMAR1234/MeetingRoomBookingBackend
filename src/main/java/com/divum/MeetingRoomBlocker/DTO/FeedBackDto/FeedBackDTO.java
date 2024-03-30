package com.divum.MeetingRoomBlocker.DTO.FeedBackDto;

import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedBackDTO {
    @NotBlank(message = "Feedback is required")
    private String Feedback;
    @NotNull
    @Min(0)
    private Integer Rating;
    @NotNull
    private long MeetingId;
    private String hostName;
    private Timestamp timestamp;
   private String roomName;

}
