package ru.alferatz.ftserver.chat;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

  private final KafkaTemplate<String, Message> kafkaTemplate;

  @PostMapping(value = "/api/send", consumes = "application/json", produces = "application/json")
  public void sendMessage(@RequestBody Message message) {
    message.setTimestamp(LocalDateTime.now().toString());
    try {
      //Sending the message to kafka topic queue
      kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, message).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

}
