package pl.ksikora.filmreviewerbackend.spotify;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ksikora.filmreviewerbackend.user.UserEntity;

import java.util.Optional;

public interface SpotifyCredentialsRepository extends JpaRepository<SpotifyCredentialsEntity, Long> {
    Optional<SpotifyCredentialsEntity> findByUser(UserEntity user);
}
