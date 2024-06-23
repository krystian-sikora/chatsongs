package pl.ksikora.filmreviewerbackend.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.auth.AuthenticationFacade;
import pl.ksikora.filmreviewerbackend.chat.message.MessageDTO;
import pl.ksikora.filmreviewerbackend.chat.message.MessageEntity;
import pl.ksikora.filmreviewerbackend.chat.message.MessageRepository;
import pl.ksikora.filmreviewerbackend.user.UserEntity;
import pl.ksikora.filmreviewerbackend.user.UserRepository;
import pl.ksikora.filmreviewerbackend.user.exceptions.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final MessageRepository messageRepository;

    public ChatDTO createChat(List<Long> userIds) {

        // TODO: don't let user create chat with himself only and automatically add current user to chat if not present

        UserEntity currentUser = authenticationFacade.getCurrentUser();

        List<UserEntity> users = userRepository.findAllByIdIn(userIds)
                .orElseThrow(UserNotFoundException::new);

        users.stream().filter(user -> user.getId().equals(currentUser.getId()))
                .findAny()
                .ifPresentOrElse(
                        user -> log.info("User is in chat"),
                        () -> users.add(currentUser)
                );

        String name = getDefaultName(users);

        ChatEntity chat = ChatEntity.builder()
                .type(ChatType.GROUP)
                .users(users)
                .name(name)
                .build();

        chatRepository.save(chat);

        List<MessageDTO> messages = List.of();

        if (chat.getMessages() != null) {
            messages = chat.getMessages().stream()
                    .map(MessageEntity::toDTO)
                    .toList();
        }

        return ChatDTO.builder()
                .id(chat.getId())
                .name(chat.getName())
                .users(chat.getUsers().stream().map(UserEntity::toDTO).toList())
                .messages(messages)
                .build();
    }

    private static String getDefaultName(List<UserEntity> users) {
        return users.stream()
                .map(UserEntity::getNickname)
                .reduce((a, b) -> a + ", " + b)
                .orElseThrow();
    }

    public List<ChatDTO> getChats() {

        UserEntity user = authenticationFacade.getCurrentUser();
        List<ChatEntity> chats = chatRepository.findAllByUsersId(user.getId());

        return chats.stream()
                .map(chat -> ChatDTO.builder()
                        .id(chat.getId())
                        .name(chat.getName())
                        .users(chat.getUsers().stream().map(UserEntity::toDTO).toList())
                        .messages(messageRepository.findAllByChatId(chat.getId()).stream()
                                .map(MessageEntity::toDTO)
                                .toList())
                        .build())
                .toList();
    }
}
