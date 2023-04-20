package ru.alferatz.ftserver.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.*;
import ru.alferatz.ftserver.model.UserDto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

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

  private LocalDateTime createTime;

  private LocalDateTime startTime;

  private String placeFrom;

  private String placeTo;

  private Integer countOfParticipants;

  private String travelStatus;

  private String comment = "";

  private Long chatId;
}
