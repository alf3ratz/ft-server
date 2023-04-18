package ru.alferatz.ftserver.chat;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
  private String id;

  private String name;

  private Long createdAt = Instant.now().toEpochMilli();
}
