package ru.alferatz.travels.repository.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
    @Builder.Default
    private Integer id = 0;

    @Builder.Default
    private String username = "";

    @Builder.Default
    private String email = "";

    @Builder.Default
    private Long travelId = 0l;


    @OneToMany(mappedBy = "user")
    @JoinColumn(name="tokens")
    private Set tokens;
}
