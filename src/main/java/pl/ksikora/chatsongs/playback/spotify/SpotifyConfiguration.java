package pl.ksikora.chatsongs.playback.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

@Service
public class SpotifyConfiguration {

    @Value("${application.frontend.url}")
    private String url;

    @Value("${application.spotify.client-id}")
    private String clientId;

    @Value("${application.spotify.client-secret}")
    private String clientSecret;

    public SpotifyApi getSpotifyObject() {
        URI redirectUri = SpotifyHttpManager.makeUri(url + "/callback");
        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }
}
