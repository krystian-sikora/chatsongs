package pl.ksikora.chatsongs.filebasedplayback.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pl.ksikora.chatsongs.user.UserEntity;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(path = "songs")
public interface SongRepository extends JpaRepository<SongEntity, UUID> {
    List<SongEntity> findAllByOwner(UserEntity owner);
}
