package pl.ksikora.chatsongs.filebasedplayback.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import pl.ksikora.chatsongs.filebasedplayback.queue.QueueSongResponse;
import pl.ksikora.chatsongs.user.UserDTO;

import java.util.List;

@Builder
@Data
public class FilebasedPlaybackSessionResponse {
    private Long id;
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("active_users")
    private List<UserDTO> activeUsers;
    @JsonProperty("song_queue")
    private List<QueueSongResponse> songQueue;
}
