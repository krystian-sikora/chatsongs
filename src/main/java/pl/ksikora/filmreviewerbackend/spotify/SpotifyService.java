package pl.ksikora.filmreviewerbackend.spotify;

import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.auth.AuthenticationFacade;
import pl.ksikora.filmreviewerbackend.spotify.exceptions.SpotifyCredentialsNotFound;
import pl.ksikora.filmreviewerbackend.user.UserEntity;
import pl.ksikora.filmreviewerbackend.user.exceptions.UserNotFoundException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SpotifyService {

    private final SpotifyConfiguration spotifyConfiguration;
    private final AuthenticationFacade authenticationFacade;
    private final SpotifyCredentialsRepository spotifyCredentialsRepository;

    public SpotifyCredentialsEntity getCredentials() {
        UserEntity user = Optional.ofNullable(authenticationFacade.getCurrentUser())
                .orElseThrow(UserNotFoundException::new);
        return spotifyCredentialsRepository.findByUser(user)
                .orElseThrow(SpotifyCredentialsNotFound::new);
    }

    public SpotifyCredentialsEntity refreshCredentials() {
        UserEntity user = Optional.ofNullable(authenticationFacade.getCurrentUser())
                .orElseThrow(UserNotFoundException::new);

        SpotifyCredentialsEntity spotifyCredentialsEntity = spotifyCredentialsRepository.findByUser(user)
                .orElseThrow(SpotifyCredentialsNotFound::new);

        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();

        try {
            spotifyApi.authorizationCodeRefresh().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }

        return updateCredentials(spotifyCredentialsEntity, spotifyApi.getAccessToken(), spotifyApi.getRefreshToken());
    }

    public String getAuthorizationUrl() {
        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();

        String state = generateRandomString(16);
        String scope = "streaming user-read-private user-read-email";

        AuthorizationCodeUriRequest authorizationCodeRequest = spotifyApi.authorizationCodeUri()
                .state(state)
                .scope(scope)
                .show_dialog(true)
                .build();

        return authorizationCodeRequest.execute().toString();
    }

    public void processCallback(String userCode) {
        UserEntity user = Optional.ofNullable(authenticationFacade.getCurrentUser())
                .orElseThrow(UserNotFoundException::new);
        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();

        AuthorizationCodeCredentials credentials;

        try {
            credentials = authorizationCodeRequest.execute();
        } catch (Exception e) {
            throw new RuntimeException("Error while processing callback");
        }

        spotifyApi.setAccessToken(credentials.getAccessToken());
        spotifyApi.setRefreshToken(credentials.getRefreshToken());

        spotifyCredentialsRepository.findByUser(user).ifPresentOrElse(
                spotifyCredentialsEntity -> updateCredentials(
                        spotifyCredentialsEntity,
                        credentials.getAccessToken(),
                        credentials.getRefreshToken()
                ),
                () -> saveCredentials(user, credentials)
        );
    }

    private SpotifyCredentialsEntity updateCredentials(
            SpotifyCredentialsEntity spotifyCredentialsEntity,
            String accessToken,
            String refreshToken
    ) {
        spotifyCredentialsEntity.setAccess_token(accessToken);
        spotifyCredentialsEntity.setRefresh_token(refreshToken);
        return spotifyCredentialsRepository.save(spotifyCredentialsEntity);
    }

    private void saveCredentials(UserEntity user, AuthorizationCodeCredentials credentials) {
        spotifyCredentialsRepository.save(SpotifyCredentialsEntity.builder()
                .user(user)
                .access_token(credentials.getAccessToken())
                .refresh_token(credentials.getRefreshToken())
                .build());
    }

    private String generateRandomString(int length) {
        StringBuilder string = new StringBuilder();
        String possibleCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < length; i++) {
            string.append(possibleCharacters.charAt((int) Math.floor(Math.random() * possibleCharacters.length())));
        }

        return string.toString();
    }
}
