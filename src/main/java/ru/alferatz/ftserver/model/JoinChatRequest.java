package ru.alferatz.ftserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinChatRequest {

  private String sessionId;
  private String particiapntId;
  private String chatId;
}
