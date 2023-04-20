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


  @JsonProperty("email")
  private String authorEmail;

  @JsonProperty("createTime")
  private LocalDateTime createTime;

  @JsonProperty("startTime")
  private LocalDateTime startTime;

  @NonNull
  @JsonProperty("placeFrom")
  private String placeFrom;

  @NonNull
  @JsonProperty("placeTo")
  private String placeTo;

  @JsonProperty("participants")
  private List<UserDto> participants;

  @JsonProperty("countOfParticipants")
  private Integer countOfParticipants;

  @Builder.Default
  private String comment = "";
  @Builder.Default
  private Long chatId = 0L;

}
