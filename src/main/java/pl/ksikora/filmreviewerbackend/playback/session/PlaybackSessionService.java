package pl.ksikora.filmreviewerbackend.playback.session;

import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.auth.AuthenticationFacade;
import pl.ksikora.filmreviewerbackend.chat.ChatEntity;
import pl.ksikora.filmreviewerbackend.chat.ChatRepository;
import pl.ksikora.filmreviewerbackend.chat.exception.ChatNotFoundException;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceEntity;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceRepository;
import pl.ksikora.filmreviewerbackend.playback.exceptions.ClientDeviceNotFoundException;
import pl.ksikora.filmreviewerbackend.playback.exceptions.SpotifyCredentialsNotFoundException;
import pl.ksikora.filmreviewerbackend.playback.spotify.SpotifyConfiguration;
import pl.ksikora.filmreviewerbackend.playback.spotify.SpotifyCredentialsEntity;
import pl.ksikora.filmreviewerbackend.playback.spotify.SpotifyCredentialsRepository;
import pl.ksikora.filmreviewerbackend.user.UserEntity;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PlaybackSessionService {
    private final AuthenticationFacade authenticationFacade;
    private final ClientDeviceRepository clientDeviceRepository;
    private final PlaybackSessionRepository playbackSessionRepository;
    private final ChatRepository chatRepository;
    private final SpotifyConfiguration spotifyConfiguration;
    private final SpotifyCredentialsRepository spotifyCredentialsRepository;

    public PlaybackSessionResponse join(Long chatId) {
        UserEntity user = authenticationFacade.getCurrentUser();
        ClientDeviceEntity device = clientDeviceRepository.findByUser(user)
                .orElseThrow(ClientDeviceNotFoundException::new);

        removeDeviceFromOtherSessions(device);

        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(ChatNotFoundException::new);

        playbackSessionRepository.findByChat(chat)
                .ifPresentOrElse(
                        session -> {
                            session.getClientDevices().add(device);
                            playbackSessionRepository.save(session);
                        },
                        () -> {
                            PlaybackSessionEntity session = PlaybackSessionEntity.builder()
                                    .chat(chat)
                                    .clientDevices(List.of(device))
                                    .build();
                            playbackSessionRepository.save(session);
                        }
                );

        PlaybackSessionEntity playback = playbackSessionRepository.findByChat(chat)
                .orElseThrow(ChatNotFoundException::new);

        synchronizePlayback(playback);

        return PlaybackSessionResponse.builder()
                .sessionId(playback.getSessionId())
                .chatId(chatId)
                .users(playback.getClientDevices().stream()
                        .map(ClientDeviceEntity::getUser)
                        .map(UserEntity::toDTO)
                        .toList())
                .build();
    }

    public Boolean quit(Long chatId) {
        UserEntity user = authenticationFacade.getCurrentUser();

        ClientDeviceEntity device = clientDeviceRepository.findByUser(user)
                .orElseThrow(ClientDeviceNotFoundException::new);

        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(ChatNotFoundException::new);

        PlaybackSessionEntity playback = playbackSessionRepository.findByChat(chat)
                .orElseThrow(ChatNotFoundException::new);
        playback.getClientDevices().remove(device);

        if (playback.getClientDevices().isEmpty()) {
            playbackSessionRepository.delete(playback);
        } else {
            playbackSessionRepository.save(playback);
        }

        return true;
    }

    private void synchronizePlayback(PlaybackSessionEntity playback) {
        Optional<UserEntity> user = playback.getClientDevices().stream()
                .map(ClientDeviceEntity::getUser)
                .filter(u -> !u.getId().equals(authenticationFacade.getCurrentUser().getId()))
                .findFirst();

        user.ifPresent(this::synchronizePlaybackWithUser);
    }

    private void synchronizePlaybackWithUser(UserEntity user) {
        SpotifyCredentialsEntity credentials = spotifyCredentialsRepository.findByUser(user)
                .orElseThrow(SpotifyCredentialsNotFoundException::new);

        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();
        spotifyApi.setRefreshToken(credentials.getRefreshToken());
        spotifyApi.setAccessToken(credentials.getAccessToken());

        try {
            CurrentlyPlaying track = spotifyApi.getUsersCurrentlyPlayingTrack().build().execute();
            if (track != null) setCurrentTrack(track);

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCurrentTrack(CurrentlyPlaying track) {
        SpotifyCredentialsEntity credentials = spotifyCredentialsRepository
                .findByUser(authenticationFacade.getCurrentUser())
                .orElseThrow(SpotifyCredentialsNotFoundException::new);

        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();
        spotifyApi.setRefreshToken(credentials.getRefreshToken());
        spotifyApi.setAccessToken(credentials.getAccessToken());

        ClientDeviceEntity device = clientDeviceRepository.findByUser(authenticationFacade.getCurrentUser())
                .orElseThrow(ClientDeviceNotFoundException::new);

        try {
            spotifyApi.startResumeUsersPlayback()
                    .context_uri(track.getContext().getUri())
                    .offset(JsonParser.parseString(String.format("{\"uri\":\"%s\"}", track.getItem().getUri())).getAsJsonObject())
                    .position_ms(track.getProgress_ms())
                    .device_id(device.getDeviceId())
                    .build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeDeviceFromOtherSessions(ClientDeviceEntity device) {
        List<PlaybackSessionEntity> sessionsWithDevice = playbackSessionRepository
                .findByClientDevicesContaining(device);

        for (PlaybackSessionEntity session : sessionsWithDevice) {
            session.getClientDevices().remove(device);
        }

        playbackSessionRepository.saveAll(sessionsWithDevice);
    }
}
