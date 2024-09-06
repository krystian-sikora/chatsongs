package pl.ksikora.chatsongs.filebasedplayback;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.ksikora.chatsongs.filebasedplayback.actions.FilebasedPlaybackActionDto;
import pl.ksikora.chatsongs.filebasedplayback.queue.QueueSongResponse;
import pl.ksikora.chatsongs.filebasedplayback.session.FilebasedPlaybackSessionResponse;
import pl.ksikora.chatsongs.filebasedplayback.song.SongService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FilebasedPlaybackController {

    private final FilebasedPlaybackService filebasedPlaybackService;
    private final SongService songService;

    @PostMapping("/api/filebased-playback")
    public ResponseEntity<FilebasedPlaybackSessionResponse> startSession(@RequestBody Long chatId) {
        return ResponseEntity.ok(filebasedPlaybackService.startSession(chatId));
    }

    @DeleteMapping("/api/filebased-playback/{id}")
    public HttpStatus quitSession(@PathVariable Long id) {
        filebasedPlaybackService.quitSession(id);
        return HttpStatus.OK;
    }

    @GetMapping("/api/filebased-playback/{id}")
    public ResponseEntity<FilebasedPlaybackSessionResponse> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(filebasedPlaybackService.getSession(id));
    }

    @PutMapping("/api/filebased-playback/{id}")
    public ResponseEntity<FilebasedPlaybackSessionResponse> addSong(@PathVariable Long id, @RequestBody String songId) {
        return ResponseEntity.ok(filebasedPlaybackService.addSong(id, UUID.fromString(songId)));
    }

    @PatchMapping("/api/filebased-playback/{id}")
    public ResponseEntity<List<QueueSongResponse>> patchSongQueue(
            @PathVariable Long id, @RequestBody List<QueueSongResponse> queue
    ) {
        return ResponseEntity.ok(filebasedPlaybackService.patchSongQueue(id, queue));
    }

    @PostMapping("/api/filebased-playback/{id}")
    public ResponseEntity<FilebasedPlaybackSessionResponse> uploadSongAndAddToSession(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long id
    ) {
        UUID songId = songService.saveUserContent(file);
        return ResponseEntity.ok(filebasedPlaybackService.addSong(id, songId));
    }

    @MessageMapping("/playback")
    public void processAction(@Payload FilebasedPlaybackActionDto actionDto, Principal user) {
        filebasedPlaybackService.processAction(actionDto, user.getName());
    }
}
