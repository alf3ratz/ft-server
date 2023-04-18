package ru.alferatz.ftserver.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

  private MessageType messageType;
  private String content;
  private String sender;
}
