package pl.ksikora.filmreviewerbackend.playback.session;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ksikora.filmreviewerbackend.chat.ChatEntity;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceEntity;

import java.util.List;
import java.util.Optional;

public interface PlaybackSessionRepository extends JpaRepository<PlaybackSessionEntity, Long> {
    Optional<PlaybackSessionEntity> findByChat(ChatEntity chat);

    List<PlaybackSessionEntity> findByClientDevicesContaining(ClientDeviceEntity clientDevice);
}
