package ru.alferatz.ftserver.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.alferatz.ftserver.chat.entity.ChatRoom;
import ru.alferatz.ftserver.repository.entity.TravelEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  Optional<ChatRoom> getChatRoomByAuthor(String userEmail);
}
