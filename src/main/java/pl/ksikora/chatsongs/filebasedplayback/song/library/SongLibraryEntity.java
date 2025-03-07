package pl.ksikora.chatsongs.filebasedplayback.song.library;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import pl.ksikora.chatsongs.filebasedplayback.song.SongEntity;

import java.util.List;

@Entity
@Data
@Table(name = "_song_library")
public class SongLibraryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @OneToMany
    private List<SongEntity> songs;
}
