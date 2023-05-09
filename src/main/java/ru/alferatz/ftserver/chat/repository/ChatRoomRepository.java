package ru.alferatz.ftserver.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alferatz.ftserver.chat.entity.ChatRoom;
import ru.alferatz.ftserver.repository.entity.TravelEntity;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  Optional<ChatRoom> getChatRoomByAuthor(String userEmail);
}
