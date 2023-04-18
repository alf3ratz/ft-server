package ru.alferatz.ftserver.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

  private String sender;
  private String content;
  private String timestamp;
  @Override
  public String toString() {
    return "Message{" +
        "sender='" + sender + '\'' +
        ", content='" + content + '\'' +
        ", timestamp='" + timestamp + '\'' +
        '}';
  }
}
