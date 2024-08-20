package pl.ksikora.chatsongs.filebasedplayback.song;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ksikora.chatsongs.auth.AuthenticationFacade;
import pl.ksikora.chatsongs.user.UserEntity;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SongService {

    private final AuthenticationFacade authenticationFacade;
    private final SongRepository songRepository;
    private final SongStore songStore;

    public UUID saveUserContent(MultipartFile file) {
        UserEntity user = authenticationFacade.getCurrentUser();
        SongEntity song = SongEntity.builder()
                .owner(user)
                .created(new Date())
                .name(file.getOriginalFilename())
                .contentMimeType(file.getContentType())
                .build();

        try {
            song = songStore.setContent(song, file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        songRepository.save(song);

        return song.getId();
    }

    public void deleteSong(UUID id) {
        UserEntity currentUser = authenticationFacade.getCurrentUser();

        SongEntity song = songRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));

        if (!song.getOwner().equals(currentUser)) {
            throw new IllegalArgumentException("Song does not belong to the current user");
        }

        songRepository.delete(song);
        songStore.unsetContent(song);
    }
}
