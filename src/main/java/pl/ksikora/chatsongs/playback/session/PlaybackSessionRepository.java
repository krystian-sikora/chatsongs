package pl.ksikora.chatsongs.playback.session;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ksikora.chatsongs.chat.ChatEntity;
import pl.ksikora.chatsongs.playback.device.ClientDeviceEntity;

import java.util.List;
import java.util.Optional;

public interface PlaybackSessionRepository extends JpaRepository<PlaybackSessionEntity, Long> {
    Optional<PlaybackSessionEntity> findByChat(ChatEntity chat);

    List<PlaybackSessionEntity> findByClientDevicesContaining(ClientDeviceEntity clientDevice);
}
