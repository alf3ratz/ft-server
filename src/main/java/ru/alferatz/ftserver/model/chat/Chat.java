package ru.alferatz.ftserver.model.chat;

import java.util.Date;
import javax.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import ru.alferatz.ftserver.utils.RandomIdGenerator;
import ru.alferatz.ftserver.utils.enums.MessageStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat implements Serializable {

  @Builder.Default
  String id = RandomIdGenerator.generate();

  String name;

  @Builder.Default
  Long createdAt = Instant.now().toEpochMilli();
//  @Id
//  private String id;
//  private String chatId;
//  private String senderId;
//  private String recipientId;
//  private String senderName;
//  private String recipientName;
//  private String content;
//  private Date timestamp;
//  private MessageStatus status;
}
