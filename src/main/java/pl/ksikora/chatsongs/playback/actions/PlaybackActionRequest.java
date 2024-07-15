package pl.ksikora.chatsongs.playback.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlaybackActionRequest {
    @JsonProperty("chat_id")
    private Long chatId;
    private PlaybackActions action;
}
