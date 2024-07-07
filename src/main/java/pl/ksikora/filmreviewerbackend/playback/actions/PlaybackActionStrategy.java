package pl.ksikora.filmreviewerbackend.playback.actions;

import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceEntity;
import se.michaelthelin.spotify.SpotifyApi;

public interface PlaybackActionStrategy {
    void execute(SpotifyApi spotifyApi, ClientDeviceEntity clientDevice);

    PlaybackActions getAction();
}
