package ru.alferatz.ftserver.chat;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class ChatDtoFactory {

  public ChatDto makeChatDto(Chat chat) {
    return ChatDto.builder()
        .id(chat.getId())
        .name(chat.getName())
        .createdAt(Instant.ofEpochMilli(chat.getCreatedAt()).toEpochMilli())
        .build();
  }
}
