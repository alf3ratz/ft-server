package ru.alferatz.ftserver.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
/**
 * Контроллер для отслеживания входа и выхода из чата
 */
public class ParticipantWsController {

  private final ParticipantService participantService;
  private final SimpMessagingTemplate messagingTemplate;

  @SubscribeMapping("/topic/chat.{chatId}.join")
  public ParticipantDto fetchParticipantJoinChat() {
    return null;
  }

  @SubscribeMapping("/topic/chat.{chatId}.leave")
  public ParticipantDto fetchParticipantLeaveChat() {
    return null;
  }
}
