package kr.co.playplace.repository.chatbot;

import kr.co.playplace.entity.chatbot.ChatbotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotMessageRepository extends JpaRepository<ChatbotMessage, Long> {
    List<ChatbotMessage> findAllByUser_Id(long userId);
}
