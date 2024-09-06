package pl.ksikora.chatsongs.filebasedplayback.queue;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueSongRepository extends JpaRepository<QueueSongEntity, Long> {
}
