package pl.ksikora.chatsongs.playback.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

@Service
public class SpotifyConfiguration {

    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:5173/callback");

    @Value("${application.spotify.client-id}")
    private String clientId;

    @Value("${application.spotify.client-secret}")
    private String clientSecret;

    public SpotifyApi getSpotifyObject() {
        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }
}
