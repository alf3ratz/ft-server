package ru.alferatz.ftserver.chat.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alferatz.ftserver.chat.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  Optional<List<ChatMessage>> getAllByChatId(Long chatId);

}
