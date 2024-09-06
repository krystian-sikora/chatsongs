package pl.ksikora.chatsongs.filebasedplayback.queue;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.chatsongs.filebasedplayback.song.SongEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "_queue_song")
public class QueueSongEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private SongEntity song;
    private Integer position;
}
