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
public class Chat {
  private String id;

  private String name;

  @Builder.Default
  private Long createdAt = Instant.now().toEpochMilli();
}
