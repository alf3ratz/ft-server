package ru.alferatz.ftserver.model.chat;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
  private String from;
  private String type;
  private String message;
  private String sender;

  @Builder.Default
  private Instant createdAt = Instant.now();
}
