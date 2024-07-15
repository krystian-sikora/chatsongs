package pl.ksikora.chatsongs.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ksikora.chatsongs.auth.AuthenticationFacade;
import pl.ksikora.chatsongs.chat.message.MessageDTO;
import pl.ksikora.chatsongs.chat.message.MessageEntity;
import pl.ksikora.chatsongs.chat.message.MessageRepository;
import pl.ksikora.chatsongs.user.UserEntity;
import pl.ksikora.chatsongs.user.UserRepository;
import pl.ksikora.chatsongs.user.exceptions.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final MessageRepository messageRepository;

    public ChatDTO createChat(List<Long> userIds) {

        UserEntity currentUser = authenticationFacade.getCurrentUser();

        List<UserEntity> users = userRepository.findAllByIdIn(userIds)
                .orElseThrow(UserNotFoundException::new);

        addCurrentUserIfNotPresent(users, currentUser);

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

    private static void addCurrentUserIfNotPresent(List<UserEntity> users, UserEntity currentUser) {
        users.stream().filter(user -> user.getId().equals(currentUser.getId()))
                .findAny()
                .ifPresentOrElse(
                        user -> {},
                        () -> users.add(currentUser)
                );
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

    private static String getDefaultName(List<UserEntity> users) {
        return users.stream()
                .map(UserEntity::getNickname)
                .reduce((a, b) -> a + ", " + b)
                .orElseThrow();
    }
}
