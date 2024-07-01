package pl.ksikora.filmreviewerbackend.spotify;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/spotify")
@AllArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final ModelMapper modelMapper;

    @GetMapping("/credentials")
    public ResponseEntity<SpotifyCredentialsDTO> getSpotify() {
        return ResponseEntity.ok(
                modelMapper.map(spotifyService.getCredentials(), SpotifyCredentialsDTO.class)
        );
    }

    @GetMapping("/refresh")
    public ResponseEntity<SpotifyCredentialsDTO> refreshSpotify() {
        return ResponseEntity.ok(
                modelMapper.map(spotifyService.refreshCredentials(), SpotifyCredentialsDTO.class)
        );
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok(spotifyService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public ModelAndView callback(@RequestParam("code") String userCode) {
        spotifyService.processCallback(userCode);
        return new ModelAndView("redirect:http://localhost:5173");
    }
}
