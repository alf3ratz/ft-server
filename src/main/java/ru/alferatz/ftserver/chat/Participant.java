package ru.alferatz.ftserver.chat;

import java.io.Serializable;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Participant implements Serializable {

  @Builder.Default
  Long enterAt = Instant.now().toEpochMilli();

  String id;

  String username;

  String chatId;
}

