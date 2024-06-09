package pl.ksikora.filmreviewerbackend.chat.message;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.filmreviewerbackend.chat.ChatEntity;

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
}
