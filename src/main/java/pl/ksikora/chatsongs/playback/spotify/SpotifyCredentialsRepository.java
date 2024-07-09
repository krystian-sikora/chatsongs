package pl.ksikora.chatsongs.playback.spotify;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ksikora.chatsongs.user.UserEntity;

import java.util.Optional;

public interface SpotifyCredentialsRepository extends JpaRepository<SpotifyCredentialsEntity, Long> {
    Optional<SpotifyCredentialsEntity> findByUser(UserEntity user);
}
