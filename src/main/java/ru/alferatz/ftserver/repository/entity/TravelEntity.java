package ru.alferatz.ftserver.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "travel_jn")
public class TravelEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String author;

  @JsonProperty("createTime")
  private LocalDateTime createTime;

  @JsonProperty("startTime")
  private LocalDateTime startTime;

  private String placeFrom;

  private String placeTo;

  private Integer countOfParticipants;

  private String travelStatus;

  private String comment = "";

  private Long chatId;
}
