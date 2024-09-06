package pl.ksikora.chatsongs.filebasedplayback.queue;

import lombok.Builder;
import lombok.Data;
import pl.ksikora.chatsongs.filebasedplayback.song.SongResponse;

@Builder
@Data
public class QueueSongResponse {
    private Long id;
    private SongResponse song;
    private Integer position;
}
