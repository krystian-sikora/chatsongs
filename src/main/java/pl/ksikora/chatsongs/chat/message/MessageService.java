package pl.ksikora.chatsongs.chat.message;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ksikora.chatsongs.chat.ChatEntity;
import pl.ksikora.chatsongs.chat.ChatRepository;
import pl.ksikora.chatsongs.user.UserRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageEntity saveMessage(MessageRequest receivedMessage, String email) {

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
