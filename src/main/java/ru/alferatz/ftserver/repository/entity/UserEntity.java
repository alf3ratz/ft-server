package ru.alferatz.ftserver.repository.entity;

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
@Table(name = "user_jn")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    private String username = "";

    private String email = "";

    private Long travelId = null;
}
