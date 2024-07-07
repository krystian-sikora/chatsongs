package pl.ksikora.filmreviewerbackend.playback.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.filmreviewerbackend.user.UserDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaybackSessionResponse {
    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("chat_id")
    private Long chatId;
    private List<UserDTO> users;
}
