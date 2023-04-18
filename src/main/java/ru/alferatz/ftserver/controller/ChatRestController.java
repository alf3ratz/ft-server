package ru.alferatz.ftserver.controller;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.chat.ChatDto;
import ru.alferatz.ftserver.chat.ChatDtoFactory;
import ru.alferatz.ftserver.chat.ChatService;


//@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ChatRestController {

  ChatService chatService;

  ChatDtoFactory chatDtoFactory;

  public static final String FETCH_CHATS = "/api/chats";

  @GetMapping(value = FETCH_CHATS, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ChatDto> fetchChats() {
    return chatService
        .getChats()
        .map(chatDtoFactory::makeChatDto)
        .collect(Collectors.toList());
  }
//
//  @PostMapping(value = "api/createChat", produces = MediaType.APPLICATION_JSON_VALUE)
//  public void createChat(@RequestParam(value = "chatName") String chatName) {
//    chatService.createChat(chatName);
//  }

//  @PostMapping(value = "api/sendMessage", produces = MediaType.APPLICATION_JSON_VALUE)
//  public void sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
//    chatService.sendMessage(sendMessageRequest);
//  }
}
