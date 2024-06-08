package pl.ksikora.filmreviewerbackend.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<Long> createChat(
            @RequestBody List<Long> userIds
    ) {
        return ResponseEntity.ok(chatService.createChat(userIds));
    }

    @GetMapping
    public ResponseEntity<List<ChatEntity>> getChats() {
        // TODO: Pagination
        return ResponseEntity.ok(chatService.getChats());
    }
}
