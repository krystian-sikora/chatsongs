package pl.ksikora.filmreviewerbackend.contact;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.auth.AuthenticationFacade;
import pl.ksikora.filmreviewerbackend.user.UserDTO;
import pl.ksikora.filmreviewerbackend.user.UserRepository;
import pl.ksikora.filmreviewerbackend.user.UserService;
import pl.ksikora.filmreviewerbackend.user.exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;
    private final UserRepository userRepository;

    public Long addContact(Long invitedUserId) {

        Long userId = authenticationFacade.getCurrentUser().getId();

        if (!userRepository.existsById(invitedUserId)) {
            throw new UserNotFoundException();
        }

        if (userId.equals(invitedUserId)) {
            throw new IllegalArgumentException("You can't add yourself to contacts");
        }

        // TODO: implement invitation logic instead of adding contact directly

        ContactEntity contact = ContactEntity.builder()
                .id(ContactPK.builder()
                        .userId(userId)
                        .contactId(invitedUserId)
                        .build())
                .build();

        contactRepository.save(contact);

        return invitedUserId;
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
