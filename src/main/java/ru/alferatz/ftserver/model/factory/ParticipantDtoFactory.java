package ru.alferatz.ftserver.model.factory;

import java.time.Instant;
import org.springframework.stereotype.Component;
import ru.alferatz.ftserver.model.chat.Participant;
import ru.alferatz.ftserver.model.chat.ParticipantDto;

@Component
public class ParticipantDtoFactory {

  public ParticipantDto makeParticipantDto(Participant participant) {
    return ParticipantDto.builder()
        .id(participant.getId())
        .enterAt(Instant.ofEpochMilli(participant.getEnterAt()))
        .build();
  }
}

