package ru.alferatz.ftserver.chat;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
  private String from;
  private String message;

  @Builder.Default
  private Instant createdAt = Instant.now();
}
