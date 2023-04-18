package ru.alferatz.ftserver.chat.entity;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageDtoFactory {

  public ChatMessageDto makeChatDto(ChatMessage message) {
    return ChatMessageDto.builder()
        .message(message.getMessage())
        .sender(message.getSender())
        .build();
  }
}
