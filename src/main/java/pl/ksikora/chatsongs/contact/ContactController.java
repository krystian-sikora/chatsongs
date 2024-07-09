package pl.ksikora.chatsongs.contact;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ksikora.chatsongs.user.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@AllArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping()
    public ResponseEntity<Long> addContact(@RequestBody String userIdb) {
        return ResponseEntity.ok(contactService.addContact(Long.valueOf(userIdb)));
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getContacts() {
        return ResponseEntity.ok(contactService.getContacts());
    }
}
