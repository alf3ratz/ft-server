package ru.alferatz.travels.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
  private Long id = 0L;

  private String author = "";

  private String placeFrom = "";

  private String placeTo = "";

  private Integer countOfParticipants = 0;

  private String travelStatus = "";

  private String comment = "";
}
