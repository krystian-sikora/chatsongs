package pl.ksikora.chatsongs.filebasedplayback.music;

import org.springframework.content.commons.store.ContentStore;
import org.springframework.content.rest.StoreRestResource;

@StoreRestResource(path="music")
public interface MusicStore extends ContentStore<MusicEntity, Long> {
}
