package pl.ksikora.chatsongs.filebasedplayback.song;

import org.springframework.content.commons.store.ContentStore;
import org.springframework.content.rest.StoreRestResource;

import java.util.UUID;

@StoreRestResource(path = "songs")
public interface SongStore extends ContentStore<SongEntity, UUID> {
}
