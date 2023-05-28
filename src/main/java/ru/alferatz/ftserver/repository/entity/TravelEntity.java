package ru.alferatz.ftserver.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.alferatz.ftserver.model.CoordsObject;


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

  @Convert(converter = MyConverter.class)
  @JsonProperty("place_from_coords")
  //@Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private CoordsObject placeFromCoords;

  @Convert(converter = MyConverter.class)
  @JsonProperty("place_to_coords")
  //@Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private CoordsObject placeToCoords;
}
