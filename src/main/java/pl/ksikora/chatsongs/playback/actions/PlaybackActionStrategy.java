package pl.ksikora.chatsongs.playback.actions;

import pl.ksikora.chatsongs.playback.device.ClientDeviceEntity;
import se.michaelthelin.spotify.SpotifyApi;

public interface PlaybackActionStrategy {
    void execute(SpotifyApi spotifyApi, ClientDeviceEntity clientDevice);

    PlaybackActions getAction();
}
