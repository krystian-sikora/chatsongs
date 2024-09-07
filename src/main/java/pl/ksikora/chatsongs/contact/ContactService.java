package pl.ksikora.chatsongs.contact;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ksikora.chatsongs.auth.AuthenticationFacade;
import pl.ksikora.chatsongs.contact.exceptions.UserAlreadyInContacts;
import pl.ksikora.chatsongs.user.UserDTO;
import pl.ksikora.chatsongs.user.UserEntity;
import pl.ksikora.chatsongs.user.UserRepository;
import pl.ksikora.chatsongs.user.UserService;
import pl.ksikora.chatsongs.user.exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;
    private final UserRepository userRepository;

    public UserDTO addContact(Long invitedUserId) {

        Long userId = authenticationFacade.getCurrentUser().getId();

        if (userId.equals(invitedUserId)) {
            throw new IllegalArgumentException("You can't add yourself to contacts");
        }

        UserEntity invitedUser = userRepository.findById(invitedUserId)
                .orElseThrow(UserNotFoundException::new);

        // TODO: implement invitation logic instead of adding contact directly

        ContactEntity contact = ContactEntity.builder()
                .id(ContactPK.builder()
                        .userId(userId)
                        .contactId(invitedUserId)
                        .build())
                .build();

        var contacts = contactRepository.getAllById(userId);

        contacts.stream()
                .filter(c -> c.getId().getContactId().equals(invitedUserId))
                .findAny()
                .ifPresent(c -> {
                    throw new UserAlreadyInContacts();
                });

        contactRepository.save(contact);

        return invitedUser.toDTO();
    }

    public List<UserDTO> getContacts() {

        Long userId = authenticationFacade.getCurrentUser().getId();
        List<ContactEntity> contacts = contactRepository.getAllById(userId);

        List<Long> userIds = contacts.stream()
                .map(ContactEntity::getId)
                .flatMap(contactPK -> Stream.of(contactPK.getUserId(), contactPK.getContactId()))
                .filter(id -> !id.equals(userId))
                .toList();

        return userService.getUsersByIds(userIds);
    }
}
