package pl.ksikora.chatsongs.chat.message;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.chatsongs.chat.ChatEntity;
import pl.ksikora.chatsongs.user.UserEntity;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_chat_message")
public class MessageEntity {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ChatEntity chat;
    private Instant createdAt;
    private String content;
    private Long senderId;

    public MessageDTO toDTO() {
        return MessageDTO.builder()
                .id(id)
                .chatId(chat.getId())
                .content(content)
                .createdAt(createdAt)
                .senderId(senderId)
                .senderName(chat.getUsers().stream()
                        .filter(u -> u.getId().equals(senderId))
                        .findFirst()
                        .map(UserEntity::getNickname)
                        .orElse("Unknown"))
                .build();
    }
}
