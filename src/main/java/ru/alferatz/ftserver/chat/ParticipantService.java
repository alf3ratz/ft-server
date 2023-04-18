package ru.alferatz.ftserver.chat;


import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ParticipantService {

  private final SetOperations<String, Participant> setOperations;
  private final SimpMessagingTemplate messagingTemplate;
  private static final Map<String, Participant> participantMap = new ConcurrentHashMap<>();

  public void handleSubscribe(String chatId, String participantId) {
    Participant participant = Participant.builder()
        .id(participantId)
        .chatId(chatId)
        .build();
    participantMap.put(participantId, participant);
    setOperations.add(
        ParticipantKeyHelper.makeKey(chatId), participant
    );
    messagingTemplate.convertAndSend(
        "/topic/chat.{chatId}.join".replace("{chatId}", chatId),
        ParticipantDto.builder()
            .id(participantId)
            .enterAt(participant.getEnterAt())
    );
  }

  @EventListener
  public void handleUnsubscribe(String chatId, String participantId) {
    Optional
        .ofNullable(participantId)
        .map(participantMap::remove)
        .ifPresent(participant -> {
          String key = ParticipantKeyHelper.makeKey(chatId);
          setOperations.remove(key, participant);
          Optional
              .ofNullable(setOperations.size(key))
              .filter(size -> size == 0L)
              .ifPresent(size -> chatService.deleteChat(chatId));

          messagingTemplate.convertAndSend(
              key,
              ParticipantDto.builder()
                  .id(participantId)
                  .enterAt(participant.getEnterAt())
          );
        });
  }

  public Set<Participant> getParticipants(String chatId) {
    return Optional.ofNullable(setOperations.members(ParticipantKeyHelper.makeKey(chatId)))
        .orElseGet(HashSet::new);
  }

  private static class ParticipantKeyHelper {

    private static final String KEY = "ru:alferatz:ft-server:chats:{chatId}:participants";

    public static String makeKey(String chatId) {
      return KEY.replace("{chatId}", chatId);
    }
  }

}
