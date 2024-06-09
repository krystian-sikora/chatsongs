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

    public ChatDTO createChat(List<Long> userIds) {

        // TODO: don't let user create chat with himself only and automatically add current user to chat if not present

        List<UserEntity> users = userRepository.findAllByIdIn(userIds)
                .orElseThrow(() -> new IllegalArgumentException("Users not found"));

        String name = users.stream()
                .map(UserEntity::getNickname)
                .reduce((a, b) -> a + ", " + b)
                .orElseThrow();

        ChatEntity chat = ChatEntity.builder()
                .type(ChatType.GROUP)
                .users(users)
                .name(name)
                .build();

        chatRepository.save(chat);

        return ChatDTO.builder()
                .id(chat.getId())
                .name(chat.getName())
                .users(chat.getUsers().stream().map(UserEntity::toDTO).toList())
                .messages(chat.getMessages())
                .build();
    }

    public List<ChatDTO> getChats() {

        UserEntity user = authenticationFacade.getCurrentUser();
        List<ChatEntity> chats = chatRepository.findAllByUsersId(user.getId());

        return chats.stream()
                .map(chat -> ChatDTO.builder()
                        .id(chat.getId())
                        .name(chat.getName())
                        .users(chat.getUsers().stream().map(UserEntity::toDTO).toList())
                        .messages(chat.getMessages())
                        .build())
                .toList();
    }
}
