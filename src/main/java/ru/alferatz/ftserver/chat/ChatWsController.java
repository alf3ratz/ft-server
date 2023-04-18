package ru.alferatz.ftserver.chat;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

  private static final Logger logger = LoggerFactory.getLogger(ChatWsController.class);
  private final SimpMessageSendingOperations messagingTemplate;
  private final ChatService chatService;
  private final ParticipantService participantService;

  @MessageMapping("/chat.create")
//  @SendTo("/topic/chat.create")
  // TODO: заменить chatMessage просто на chatId
  public void createChat(/*@DestinationVariable("chatName")*/ @Payload Message chatMessage) {
    chatService.createChat(chatMessage);
  }


  @MessageMapping("/chat.{chatId}.send")
  public Message sendMessage(@DestinationVariable("chatId") String chatId,
      @Payload Message chatMessage) {
    logger.info(chatId + " Chat message recieved is " + chatMessage.getContent());
    messagingTemplate.convertAndSend(
        format("/topic/chat.%s.send", chatId),
        MessageDto.builder()
            .from(chatMessage.getSender())
            .message(chatMessage.getContent())
            .build()
    );
    return chatMessage;
  }

  @MessageMapping("/chat.{chat_id}.messages")
  //"/topic/chat.{chat_id}.messages"
  public MessageDto fetchMessages(@DestinationVariable("chatId") String chatId,
      @Payload String participantId) {
    participantService.handleSubscribe(chatId, participantId);
  }
//
//  @MessageMapping("/chat/{roomId}/addUser")
//  @SendTo("/topic/")
//  public Message addUser(@DestinationVariable String roomId, @Payload Message chatMessage,
//      SimpMessageHeaderAccessor headerAccessor) {
//    String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
//    if (currentRoomId != null) {
//      Message leaveMessage = new Message();
//      leaveMessage.setMessageType(MessageType.LEAVE);
//      leaveMessage.setSender(chatMessage.getSender());
//      messagingTemplate.convertAndSend(format("/topic/%s", currentRoomId), leaveMessage);
//    }
//    headerAccessor.getSessionAttributes().put("name", chatMessage.getSender());
//    messagingTemplate.convertAndSend(format("/topic/%s", roomId), chatMessage);
//    return chatMessage;
//  }

//  @SendTo("/topic/message")
//  public Message broadcastMessage(@Payload Message chatMessage) {
//    return chatMessage;
//  }
}