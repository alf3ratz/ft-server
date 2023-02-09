package ru.alferatz.ftserver.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TravelEntity  {
    @Id
    @Builder.Default
    private Long id = 0L;

    @Builder.Default
    private String author = "";

    @Builder.Default
    private String placeFrom = "";

    @Builder.Default
    private String placeTo = "";

    @Builder.Default
    private Integer countOfParticipants = 0;

    @Builder.Default
    private String travelStatus = "";
}
