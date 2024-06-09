package pl.ksikora.filmreviewerbackend.chat.message;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.chat.ChatEntity;
import pl.ksikora.filmreviewerbackend.chat.ChatRepository;
import pl.ksikora.filmreviewerbackend.user.UserRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageEntity saveMessage(MessageDTO receivedMessage, String email) {

        Long id = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();

        ChatEntity chat = chatRepository.findById(receivedMessage.getChatId())
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        MessageEntity message = MessageEntity.builder()
                .chat(chat)
                .createdAt(Instant.now())
                .content(receivedMessage.getContent())
                .senderId(id)
                .build();

        return messageRepository.save(message);
    }
}
