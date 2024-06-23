package pl.ksikora.filmreviewerbackend.chat.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String content;
    private Instant createdAt;
}
