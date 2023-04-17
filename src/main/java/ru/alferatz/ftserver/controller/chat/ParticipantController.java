package ru.alferatz.ftserver.controller.chat;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.requests.JoinChatRequest;
import ru.alferatz.ftserver.model.chat.ParticipantDto;
import ru.alferatz.ftserver.model.factory.ParticipantDtoFactory;
import ru.alferatz.ftserver.service.chat.ParticipantService;

//@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ParticipantController {

  ParticipantService participantService;

  ParticipantDtoFactory participantDtoFactory;

  public static final String FETCH_PARTICIPANTS = "/api/chats/{chat_id}/participants";

  @GetMapping(value = FETCH_PARTICIPANTS, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ParticipantDto> fetchParticipants(@PathVariable("chat_id") String chatId) {
    return participantService
        .getParticipants(chatId)
        .map(participantDtoFactory::makeParticipantDto)
        .collect(Collectors.toList());
  }

  @PostMapping(value = "api/chats/join", produces = MediaType.APPLICATION_JSON_VALUE)
  public void joinChat(@RequestBody JoinChatRequest joinChatRequest) {
    participantService
        .handleJoinChat(joinChatRequest.getSessionId(), joinChatRequest.getParticiapntId(),
            joinChatRequest.getChatId());
  }

}

