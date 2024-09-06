package pl.ksikora.chatsongs.filebasedplayback;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.ksikora.chatsongs.auth.AuthenticationFacade;
import pl.ksikora.chatsongs.chat.ChatEntity;
import pl.ksikora.chatsongs.chat.ChatRepository;
import pl.ksikora.chatsongs.filebasedplayback.actions.FilebasedPlaybackActionDto;
import pl.ksikora.chatsongs.filebasedplayback.actions.FilebasedPlaybackActions;
import pl.ksikora.chatsongs.filebasedplayback.queue.QueueSongRepository;
import pl.ksikora.chatsongs.filebasedplayback.queue.QueueSongResponse;
import pl.ksikora.chatsongs.filebasedplayback.session.FilebasedPlaybackRepository;
import pl.ksikora.chatsongs.filebasedplayback.session.FilebasedPlaybackSessionEntity;
import pl.ksikora.chatsongs.filebasedplayback.session.FilebasedPlaybackSessionResponse;
import pl.ksikora.chatsongs.filebasedplayback.queue.QueueSongEntity;
import pl.ksikora.chatsongs.filebasedplayback.queue.QueueSongMapper;
import pl.ksikora.chatsongs.filebasedplayback.song.SongEntity;
import pl.ksikora.chatsongs.filebasedplayback.song.SongRepository;
import pl.ksikora.chatsongs.user.UserEntity;
import pl.ksikora.chatsongs.user.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FilebasedPlaybackService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final FilebasedPlaybackRepository filebasedPlaybackRepository;
    private final AuthenticationFacade authenticationFacade;
    private final SongRepository songRepository;
    private final QueueSongRepository queueSongRepository;
    private final SimpMessagingTemplate template;

    @Transactional
    public void processAction(FilebasedPlaybackActionDto action, String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();

        ChatEntity chat = chatRepository.findById(action.getChatId())
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        FilebasedPlaybackSessionEntity session = filebasedPlaybackRepository.findByChat(chat)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        sendAction(session, userId, action);
    }

    public FilebasedPlaybackSessionResponse getSession(Long chatId) {
        UserEntity user = authenticationFacade.getCurrentUser();
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        FilebasedPlaybackSessionEntity session = filebasedPlaybackRepository.findByChat(chat)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        throwIfUserNotInSession(session, user);

        return buildFilebasedPlaybackSessionResponse(session);
    }

    public FilebasedPlaybackSessionResponse startSession(Long chatId) {
        UserEntity user = authenticationFacade.getCurrentUser();
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Optional<FilebasedPlaybackSessionEntity> session = filebasedPlaybackRepository.findByChat(chat);

        quitUserIfInSession(user);

        if (session.isPresent()) {
            FilebasedPlaybackSessionEntity s = session.get();
            s.getActiveUsers().stream()
                    .filter(u -> u.getId().equals(user.getId()))
                    .findAny()
                    .ifPresentOrElse(
                            u -> { },
                            () -> s.getActiveUsers().add(user)
                    );

            filebasedPlaybackRepository.save(session.get());

            return buildFilebasedPlaybackSessionResponse(s);
        }

        FilebasedPlaybackSessionEntity newSession = FilebasedPlaybackSessionEntity.builder()
                .chat(chat)
                .activeUsers(new ArrayList<>(List.of(user)))
                .songQueue(new ArrayList<>())
                .build();

        newSession = filebasedPlaybackRepository.save(newSession);

        return buildFilebasedPlaybackSessionResponse(newSession);
    }

    private void quitUserIfInSession(UserEntity user) {
        filebasedPlaybackRepository.findByActiveUsersId(user.getId())
                .ifPresent(pb -> {
                    pb.getActiveUsers().remove(user);
                    filebasedPlaybackRepository.save(pb);
                });
    }

    private static FilebasedPlaybackSessionResponse buildFilebasedPlaybackSessionResponse(FilebasedPlaybackSessionEntity s) {
        return FilebasedPlaybackSessionResponse.builder()
                .id(s.getId())
                .chatId(s.getChat().getId())
                .activeUsers(
                        s.getActiveUsers().stream()
                                .map(UserEntity::toDTO)
                                .toList()
                )
                .songQueue(s.getSongQueue().isEmpty() ? new ArrayList<>() :
                        s.getSongQueue().stream()
                                .sorted(Comparator.comparingInt(QueueSongEntity::getPosition))
                                .map(QueueSongMapper.INSTANCE::toResponse)
                                .toList()
                )
                .build();
    }

    public FilebasedPlaybackSessionResponse addSong(Long id, UUID songId) {
        UserEntity user = authenticationFacade.getCurrentUser();
        FilebasedPlaybackSessionEntity session = filebasedPlaybackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        throwIfUserNotInSession(session, user);

        SongEntity song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));

        QueueSongEntity queueSong = QueueSongEntity.builder()
                .song(song)
                .position(session.getSongQueue().size())
                .build();

        queueSongRepository.save(queueSong);
        session.getSongQueue().add(queueSong);

        sendAction(session, user.getId(), FilebasedPlaybackActionDto.builder()
                .chatId(session.getChat().getId())
                .action(FilebasedPlaybackActions.UPDATE)
                .timestamp(Instant.now())
                .build());

        return buildFilebasedPlaybackSessionResponse(filebasedPlaybackRepository.save(session));
    }

    private void sendAction(FilebasedPlaybackSessionEntity session, Long user, FilebasedPlaybackActionDto action) {
        session.getActiveUsers().stream()
                .map(u -> u.getId().toString())
                .filter(uId -> !uId.equals(user.toString()))
                .forEach(uId -> template.convertAndSendToUser(
                        uId,
                        "/queue/playback", action
                ));
    }

    private static void throwIfUserNotInSession(FilebasedPlaybackSessionEntity session, UserEntity user) {
        if (!session.getActiveUsers().contains(user)) {
            throw new IllegalArgumentException("User is not a member of the session");
        }
    }

    public List<QueueSongResponse> patchSongQueue(Long id, List<QueueSongResponse> queue) {
        UserEntity user = authenticationFacade.getCurrentUser();

        ChatEntity chat = chatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        FilebasedPlaybackSessionEntity session = filebasedPlaybackRepository.findByChat(chat)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        throwIfUserNotInSession(session, user);

        session.getSongQueue().forEach(q -> q.setPosition(queue.stream()
                .filter(qr -> qr.getId().equals(q.getId()))
                .findAny()
                .orElseThrow()
                .getPosition()
        ));

        filebasedPlaybackRepository.save(session);

        return session.getSongQueue().stream()
                .map(QueueSongMapper.INSTANCE::toResponse)
                .toList();
    }

    public void quitSession(Long chatId) {
        UserEntity user = authenticationFacade.getCurrentUser();
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        FilebasedPlaybackSessionEntity session = filebasedPlaybackRepository.findByChat(chat)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.getActiveUsers().stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findAny()
                .ifPresent(u -> session.getActiveUsers().remove(u));

        filebasedPlaybackRepository.save(session);

        sendAction(session, user.getId(), FilebasedPlaybackActionDto.builder()
                .chatId(session.getChat().getId())
                .action(FilebasedPlaybackActions.UPDATE)
                .timestamp(Instant.now())
                .build());
    }
}
