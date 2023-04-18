package ru.alferatz.ftserver.chat;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

//@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ChatService {
  private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

  //ChatWsController chatWsController;

//  ChatDtoFactory chatDtoFactory;

  SimpMessagingTemplate messagingTemplate;

  SetOperations<String, Chat> setOperations;

  private static final String KEY = "may:code:crazy-chat:chats";

  public void createChat(Message chatMessage) {
    var chatId = chatMessage.getSender();
    logger.info(chatId + " Chat creation started");
    Chat chat = Chat.builder()
        .id(chatId)
        .name(chatId + "-name")
        .build();

    setOperations.add(KEY, chat);

    messagingTemplate.convertAndSend(
        "/topic/chat.create.event",
        ChatDto.builder()
            .id(chatId)
            .createdAt(chat.getCreatedAt())
            .name(chat.getName())
            .build()
    );
    logger.info(chatId + " Chat creation done");
  }

//  public void deleteChat(String chatId) {
//
//    getChats()
//        .filter(chat -> Objects.equals(chatId, chat.getId()))
//        .findAny()
//        .ifPresent(chat -> {
//
//          //log.info(String.format("Chat \"%s\" deleted.", chat.getName()));
//
//          setOperations.remove(KEY, chat);
//
//          messagingTemplate.convertAndSend(
//              ChatWsController.FETCH_DELETE_CHAT_EVENT,
//              chatDtoFactory.makeChatDto(chat)
//          );
//        });
//  }

  public Stream<Chat> getChats() {
    return Optional
        .ofNullable(setOperations.members(KEY))
        .orElseGet(HashSet::new)
        .stream();
  }

//  public void sendMessage(SendMessageRequest sendMessageRequest) {
//    chatWsController.sendMessageToAll(sendMessageRequest.getChatId(),
//        sendMessageRequest.getMessage(),
//        sendMessageRequest.getSimpSessionId());
//  }
}