package pl.ksikora.filmreviewerbackend.playback;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.auth.AuthenticationFacade;
import pl.ksikora.filmreviewerbackend.chat.ChatEntity;
import pl.ksikora.filmreviewerbackend.chat.ChatRepository;
import pl.ksikora.filmreviewerbackend.chat.exception.ChatNotFoundException;
import pl.ksikora.filmreviewerbackend.playback.actions.PlaybackActionStrategy;
import pl.ksikora.filmreviewerbackend.playback.actions.PlaybackActions;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceEntity;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceRepository;
import pl.ksikora.filmreviewerbackend.playback.exceptions.ClientDeviceNotFoundException;
import pl.ksikora.filmreviewerbackend.playback.exceptions.SessionNotFoundException;
import pl.ksikora.filmreviewerbackend.playback.exceptions.SpotifyCredentialsNotFoundException;
import pl.ksikora.filmreviewerbackend.playback.session.PlaybackSessionEntity;
import pl.ksikora.filmreviewerbackend.playback.session.PlaybackSessionRepository;
import pl.ksikora.filmreviewerbackend.playback.session.PlaybackSessionResponse;
import pl.ksikora.filmreviewerbackend.playback.spotify.SpotifyConfiguration;
import pl.ksikora.filmreviewerbackend.playback.spotify.SpotifyCredentialsEntity;
import pl.ksikora.filmreviewerbackend.playback.spotify.SpotifyCredentialsRepository;
import pl.ksikora.filmreviewerbackend.user.UserEntity;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PlaybackService {

    private final AuthenticationFacade authenticationFacade;
    private final PlaybackSessionRepository playbackSessionRepository;
    private final ChatRepository chatRepository;
    private final Map<PlaybackActions, PlaybackActionStrategy> actionStrategyMap = new HashMap<>();
    private final SpotifyConfiguration spotifyConfiguration;
    private final ClientDeviceRepository clientDeviceRepository;
    private final SpotifyCredentialsRepository spotifyCredentialsRepository;

    private static boolean isNotChatMember(ChatEntity chat, UserEntity user) {
        return chat.getUsers().stream()
                .filter(u -> u.getId().equals(user.getId()))
                .noneMatch(u -> true);
    }

    @Autowired
    public void populateStrategyMap(List<PlaybackActionStrategy> strategies) {
        for (PlaybackActionStrategy strategy : strategies) {
            actionStrategyMap.put(strategy.getAction(), strategy);
        }
    }

    public PlaybackSessionResponse getPlayback(Long chatId) {
        UserEntity user = authenticationFacade.getCurrentUser();
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(ChatNotFoundException::new);

        if (isNotChatMember(chat, user)) {
            throw new RuntimeException("User is not a member of the chat");
        }

        PlaybackSessionEntity session = playbackSessionRepository.findByChat(chat)
                .orElseThrow(SessionNotFoundException::new);

        if (session.getClientDevices().isEmpty()) {
            playbackSessionRepository.delete(session);
            throw new SessionNotFoundException();
        }

        return PlaybackSessionResponse.builder()
                .sessionId(session.getSessionId())
                .chatId(chatId)
                .users(session.getClientDevices().stream()
                        .map(ClientDeviceEntity::getUser)
                        .map(UserEntity::toDTO)
                        .toList())
                .build();
    }

    public Boolean playbackAction(PlaybackActionRequest request) {
        UserEntity user = authenticationFacade.getCurrentUser();
        ChatEntity chat = chatRepository.findById(request.getChatId())
                .orElseThrow(ChatNotFoundException::new);

        if (isNotChatMember(chat, user)) {
            throw new RuntimeException("User is not a member of the chat");
        }

        PlaybackSessionEntity session = playbackSessionRepository.findByChat(chat)
                .orElseThrow(SessionNotFoundException::new);

        PlaybackActionStrategy actionStrategy = actionStrategyMap.get(request.getAction());

        if (!isClientInSession(session, user)) {
            throw new RuntimeException("User is not a member of the session");
        }

        List<UserEntity> users = session.getClientDevices().stream()
                .filter(ClientDeviceEntity::getIsActive)
                .map(ClientDeviceEntity::getUser)
                .toList();

        for (UserEntity u : users) {
            ClientDeviceEntity clientDevice = clientDeviceRepository.findByUser(u)
                    .orElseThrow(ClientDeviceNotFoundException::new);

            SpotifyCredentialsEntity credentials = spotifyCredentialsRepository.findByUser(u)
                    .orElseThrow(SpotifyCredentialsNotFoundException::new);

            SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();
            spotifyApi.setRefreshToken(credentials.getRefreshToken());
            spotifyApi.setAccessToken(credentials.getAccessToken());

            actionStrategy.execute(spotifyApi, clientDevice);
        }

        return true;
    }

    private static boolean isClientInSession(PlaybackSessionEntity session, UserEntity user) {
        return session.getClientDevices().stream()
                .map(ClientDeviceEntity::getUser)
                .filter(u -> u.getId().equals(user.getId()))
                .anyMatch(u -> true);
    }
}
