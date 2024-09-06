package pl.ksikora.chatsongs.filebasedplayback.session;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ksikora.chatsongs.chat.ChatEntity;

import java.util.Optional;

public interface FilebasedPlaybackRepository extends JpaRepository<FilebasedPlaybackSessionEntity, Long> {
    Optional<FilebasedPlaybackSessionEntity> findByChat(ChatEntity chat);
    Optional<FilebasedPlaybackSessionEntity> findByActiveUsersId(Long userId);
}
