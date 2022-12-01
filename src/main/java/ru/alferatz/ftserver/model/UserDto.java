package ru.alferatz.ftserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    @NonNull
    @JsonProperty("username")
    private String username;

    @NonNull
    @JsonProperty("email")
    private String email;
}
