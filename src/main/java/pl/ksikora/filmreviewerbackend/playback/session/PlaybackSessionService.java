package pl.ksikora.filmreviewerbackend.playback.session;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.auth.AuthenticationFacade;
import pl.ksikora.filmreviewerbackend.chat.ChatEntity;
import pl.ksikora.filmreviewerbackend.chat.ChatRepository;
import pl.ksikora.filmreviewerbackend.chat.exception.ChatNotFoundException;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceEntity;
import pl.ksikora.filmreviewerbackend.playback.device.ClientDeviceRepository;
import pl.ksikora.filmreviewerbackend.playback.exceptions.ClientDeviceNotFoundException;
import pl.ksikora.filmreviewerbackend.user.UserEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaybackSessionService {
    private final AuthenticationFacade authenticationFacade;
    private final ClientDeviceRepository clientDeviceRepository;
    private final PlaybackSessionRepository playbackSessionRepository;
    private final ChatRepository chatRepository;

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

        return PlaybackSessionResponse.builder()
                .sessionId(playback.getSessionId())
                .chatId(chatId)
                .users(playback.getClientDevices().stream()
                        .map(ClientDeviceEntity::getUser)
                        .map(UserEntity::toDTO)
                        .toList())
                .build();
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
