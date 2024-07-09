package pl.ksikora.chatsongs.playback.actions;

import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import pl.ksikora.chatsongs.playback.device.ClientDeviceEntity;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@Component
@AllArgsConstructor
public class PlaybackActionPlayStrategy implements PlaybackActionStrategy {

    @Override
    public void execute(SpotifyApi spotifyApi, ClientDeviceEntity clientDevice) {
        try {
            spotifyApi.startResumeUsersPlayback().device_id(clientDevice.getDeviceId()).build().execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PlaybackActions getAction() {
        return PlaybackActions.PLAY;
    }
}
