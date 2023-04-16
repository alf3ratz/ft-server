package ru.alferatz.ftserver.model.factory;

import java.time.Instant;
import org.springframework.stereotype.Component;
import ru.alferatz.ftserver.model.chat.Chat;
import ru.alferatz.ftserver.model.chat.ChatDto;

@Component
public class ChatDtoFactory {

  public ChatDto makeChatDto(Chat chat) {
    return ChatDto.builder()
        .id(chat.getId())
        .name(chat.getName())
        .createdAt(Instant.ofEpochMilli(chat.getCreatedAt()))
        .build();
  }
}
