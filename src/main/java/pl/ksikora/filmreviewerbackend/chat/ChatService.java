package pl.ksikora.filmreviewerbackend.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.auth.AuthenticationFacade;
import pl.ksikora.filmreviewerbackend.user.UserEntity;
import pl.ksikora.filmreviewerbackend.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;

    public Long createChat(List<Long> userIds) {

        List<UserEntity> users = userRepository.findAllByIdIn(userIds)
                .orElseThrow(() -> new IllegalArgumentException("Users not found"));

        String name = users.stream()
                .map(UserEntity::getUsername)
                .reduce((a, b) -> a + ", " + b)
                .orElseThrow();

        ChatEntity chat = ChatEntity.builder()
                .type(ChatType.GROUP)
                .users(users)
                .name(name)
                .build();

        log.info("Creating chat: {}", chat.getId());

        chatRepository.save(chat);

        return chat.getId();
    }

    public List<ChatEntity> getChats() {

        UserEntity user = authenticationFacade.getCurrentUser();

        return chatRepository.findAllByUsersId(user.getId());
    }
}
