package ru.alferatz.ftserver.requests;

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
public class SendMessageRequest {
  private String chatId;
  private String message;
  private String simpSessionId;
}
