package pl.ksikora.chatsongs.playback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pl.ksikora.chatsongs.playback.actions.PlaybackActions;

@Data
public class PlaybackActionRequest {
    @JsonProperty("chat_id")
    private Long chatId;
    private PlaybackActions action;
}
