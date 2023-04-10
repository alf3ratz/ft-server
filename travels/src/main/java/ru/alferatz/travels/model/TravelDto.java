package ru.alferatz.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelDto {

  @NonNull
  @JsonProperty("author")
  private String author;

  @JsonProperty("email")
  private String email;

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
}
