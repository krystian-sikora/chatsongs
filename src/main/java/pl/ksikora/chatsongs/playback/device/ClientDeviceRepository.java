package pl.ksikora.chatsongs.playback.device;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ksikora.chatsongs.user.UserEntity;

import java.util.Optional;

public interface ClientDeviceRepository extends JpaRepository<ClientDeviceEntity, String> {
    Optional<ClientDeviceEntity> findByUser(UserEntity user);
}
