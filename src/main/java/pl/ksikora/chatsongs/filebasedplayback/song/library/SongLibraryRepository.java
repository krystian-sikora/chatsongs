package pl.ksikora.chatsongs.filebasedplayback.song.library;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SongLibraryRepository extends JpaRepository<SongLibraryEntity, Long> {
}
