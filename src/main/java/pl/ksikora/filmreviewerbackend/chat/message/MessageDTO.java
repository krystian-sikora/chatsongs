package pl.ksikora.filmreviewerbackend.chat.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    @JsonProperty("chat_id")
    private Long chatId;
    private String content;
}
