package pl.ksikora.filmreviewerbackend.chat.message;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_chat_message")
public class ChatMessageEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String content;
    private String sender;
}
