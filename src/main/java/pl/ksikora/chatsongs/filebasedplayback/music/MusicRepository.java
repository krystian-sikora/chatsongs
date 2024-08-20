package pl.ksikora.chatsongs.filebasedplayback.music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path="music")
public interface MusicRepository extends JpaRepository<MusicEntity, UUID> {

}
