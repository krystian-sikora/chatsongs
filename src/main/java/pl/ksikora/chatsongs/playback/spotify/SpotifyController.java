package pl.ksikora.chatsongs.playback.spotify;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

    @Value("${application.frontend.url}")
    private String url;

    @GetMapping("/credentials")
    public ResponseEntity<SpotifyCredentialsDTO> getSpotify() {
        return ResponseEntity.ok(spotifyService.getCredentials());
    }

    @GetMapping("/refresh")
    public ResponseEntity<SpotifyCredentialsDTO> refreshSpotify() {
        return ResponseEntity.ok(spotifyService.refreshCredentials());
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok(spotifyService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public ModelAndView callback(@RequestParam("code") String userCode) {
        spotifyService.processCallback(userCode);
        return new ModelAndView("redirect:" + url);
    }
}
