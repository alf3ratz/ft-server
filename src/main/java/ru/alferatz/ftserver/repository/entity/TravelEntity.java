package ru.alferatz.ftserver.repository.entity;

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
@Table(name = "travel")
public class TravelEntity {
    @Id
    @Builder.Default
    private Integer id = 0;
}
