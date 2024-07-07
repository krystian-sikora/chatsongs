package pl.ksikora.filmreviewerbackend.playback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pl.ksikora.filmreviewerbackend.playback.actions.PlaybackActions;

@Data
public class PlaybackActionRequest {
    @JsonProperty("chat_id")
    private Long chatId;
    private PlaybackActions action;
}
