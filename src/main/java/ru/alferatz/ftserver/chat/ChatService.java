package ru.alferatz.ftserver.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.alferatz.ftserver.chat.entity.ChatMessageDto;
import ru.alferatz.ftserver.chat.entity.ChatMessageDtoFactory;
import ru.alferatz.ftserver.chat.entity.ChatRoom;
import ru.alferatz.ftserver.chat.entity.ChatMessage;
import ru.alferatz.ftserver.chat.entity.GetMessagesRequest;
import ru.alferatz.ftserver.chat.repository.ChatMessageRepository;
import ru.alferatz.ftserver.chat.repository.ChatRoomRepository;
import ru.alferatz.ftserver.exceptions.AlreadyExistException;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.exceptions.NotFoundException;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class ChatService {

  //private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

  //  private final SimpMessagingTemplate messagingTemplate;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatMessageDtoFactory chatMessageDtoFactory;
  private final UserRepository userRepository;

  public void sendMessage(SendMessageRequest request) {

    String message = request.getMessage();
    Long chatId = request.getChatId();
    String sender = request.getSender();

    ChatMessage chatMessage = ChatMessage.builder()
        .chatId(chatId)
        .sender(sender)
        .message(message)
        .build();

    try {
      chatMessageRepository.save(chatMessage);
    } catch (RuntimeException e) {
      throw new InternalServerError(
          "Не удалось отправить сообщение в чат: {}".replace("{}", e.getMessage()));
    } finally {
      chatMessageRepository.flush();
    }
    // TODO: добавить общение по вебсокетам
//    messagingTemplate.convertAndSend(
//        "/topic/chat/"
//    );
//    logger
//        .info(String.format("Message [%s] sent to chat: [%d] from: [%s]", message, chatId, sender));
  }

  public Long createChatRoom(String authorEmail) {
    ChatRoom newChat = ChatRoom.builder()
        .author(authorEmail)
        .build();
    UserEntity user = userRepository.getUserEntityByEmail(authorEmail).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователь не найден в системе");
    }
    if (user.getChatId() != null) {
      throw new InternalServerError("Пользователь уже находится в активном чате");
    }
    ChatRoom chat = chatRoomRepository.getChatRoomByAuthor(authorEmail).orElse(null);
    if (chat != null) {
      throw new AlreadyExistException("Пользователь уже создал чат");
    }

    try {
      chatRoomRepository.save(newChat);
      return newChat.getId();
    } catch (RuntimeException e) {
      throw new InternalServerError("Не удалось сохранить чат: {}".replace("{}", e.getMessage()));
    } finally {
      chatRoomRepository.flush();
//      logger
//          .info(String.format("Chat with id: [%d] created by [%s]", newChat.getId(), authorEmail));
    }
  }

  public void deleteChatRoom(Long chatId) {
    ChatRoom chatForDelete = chatRoomRepository.findById(chatId).orElse(null);
    var usersInChat = userRepository.getAllByChatId(chatId).orElseGet(Collections::emptyList);
    usersInChat.forEach(i -> i.setChatId(null));
    if (chatForDelete == null) {
      throw new NotFoundException("Чат не существует");
    }
    try {
      chatRoomRepository.delete(chatForDelete);
      userRepository.saveAll(usersInChat);
    } catch (RuntimeException e) {
      throw new InternalServerError("Не удалось удалить чат: {}".replace("{}", e.getMessage()));
    } finally {
      chatRoomRepository.flush();
    }
  }

  public List<ChatMessageDto> getMessageByChat(Long chatId) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatId).orElse(null);
    if (chatRoom == null) {
      throw new NotFoundException("Чат не существует");
    }
    List<ChatMessage> chatMessages;
    List<ChatMessageDto> chatMessagesDto = new ArrayList<>();
    try {
      chatMessages = chatMessageRepository.getAllByChatId(chatId).orElseGet(
          Collections::emptyList);
      return chatMessages.stream()
          .map(chatMessageDtoFactory::makeChatDto)
          .collect(Collectors.toList());
    } catch (RuntimeException e) {
      throw new InternalServerError(
          "Не удалось получить сообщения из чата: {}".replace("{}", e.getMessage()));
    } finally {
      chatMessageRepository.flush();
    }
  }
}
