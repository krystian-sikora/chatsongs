package pl.ksikora.chatsongs.filebasedplayback.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilebasedPlaybackActionDto {
    @JsonProperty("chat_id")
    private Long chatId;
    private FilebasedPlaybackActions action;
    private Instant timestamp;
    @JsonProperty("current_time")
    private Long currentTime;
    @JsonProperty("is_playing")
    private Boolean isPlaying;
    private Integer index;
}
