package pl.ksikora.chatsongs.filebasedplayback.music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="music")
public interface MusicRepository extends JpaRepository<MusicEntity, Long> {

}
