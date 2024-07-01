package pl.ksikora.filmreviewerbackend.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpotifyCredentialsDTO {
    @JsonProperty("access_token")
    public String accessToken;
    @JsonProperty("refresh_token")
    public String refreshToken;
}
