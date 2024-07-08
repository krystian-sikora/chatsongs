package pl.ksikora.filmreviewerbackend.playback;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceRequest;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceResponse;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceService;
import pl.ksikora.filmreviewerbackend.playback.session.PlaybackSessionResponse;
import pl.ksikora.filmreviewerbackend.playback.session.PlaybackSessionService;


@RestController
@RequestMapping("/api/playback")
@AllArgsConstructor
public class PlaybackController {

    private final PlaybackSessionService playbackSessionService;
    private final PlaybackService playbackService;
    private final ClientDeviceService clientDeviceService;

    @GetMapping("/{chatId}")
    public ResponseEntity<PlaybackSessionResponse> getPlayback(@PathVariable Long chatId) {
        return ResponseEntity.ok(playbackService.getPlayback(chatId));
    }

    @PostMapping()
    public ResponseEntity<Boolean> playbackAction(@RequestBody PlaybackActionRequest request) {
        return ResponseEntity.ok(playbackService.playbackAction(request));
    }

    @PostMapping("/join")
    public ResponseEntity<PlaybackSessionResponse> join(@RequestBody Long chatId) {
        return ResponseEntity.ok(playbackSessionService.join(chatId));
    }

    @PostMapping("/quit")
    public ResponseEntity<Boolean> quit(@RequestBody Long chatId) {
        return ResponseEntity.ok(playbackSessionService.quit(chatId));
    }

    @PostMapping("/device")
    public ResponseEntity<ClientDeviceResponse> registerDevice(@RequestBody ClientDeviceRequest clientDevice) {
        return ResponseEntity.ok(clientDeviceService.registerDevice(clientDevice));
    }
}
