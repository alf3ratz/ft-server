package ru.alferatz.ftserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelDto {


  private String authorEmail;

  private LocalDateTime createTime;

  private LocalDateTime startTime;

  private String placeFrom;

  private String placeTo;

  @JsonProperty("participants")
  private List<UserDto> participants;

  private Integer countOfParticipants;

  @Builder.Default
  private String comment = "";
  @Builder.Default
  private Long chatId = 0L;

}
