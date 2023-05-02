package ru.alferatz.ftserver.chat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.chat.entity.ChatMessage;
import ru.alferatz.ftserver.chat.entity.ChatMessageDto;
import ru.alferatz.ftserver.chat.entity.GetMessagesRequest;

@RequestMapping("/api/chat")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ChatController {

  private final ChatService chatService;

//  @MessageMapping("/chat/sendMessage")
//  public void sendMessage(@Payload SendMessageRequest request) {
//    chatService.sendMessage(request);
//  }

  @PostMapping("/createChat")
  public Long createChat(@RequestParam("authorEmail") String authorEmail) {
    return chatService.createChatRoom(authorEmail);
  }

  @PostMapping("/deleteChat")
  public void deleteChat(@RequestParam("chatId") Long chatId) {
    chatService.deleteChatRoom(chatId);
  }

  @PostMapping("/sendMessage")
  public void sendMessage(@RequestBody SendMessageRequest request) {
    chatService.sendMessage(request);
  }

  @GetMapping(value = "/getMessagesByChat", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ChatMessageDto> getMessagesFromChat(@RequestParam("chatId") Long chatId) {
    var res = chatService.getMessageByChat(chatId);
//    if (res.isEmpty()) {
//      return List.of(ChatMessageDto.builder().build());
//    }
    return res;
  }
}
