package pl.ksikora.filmreviewerbackend.chat.message;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/chat")
    public void send(@Payload MessageRequest message, Principal user) {

        MessageEntity processedMessage = messageService.saveMessage(message, user.getName());

        processedMessage.getChat().getUsers().stream()
                .map(u -> u.getId().toString())
                .forEach(userId -> template.convertAndSendToUser(
                        userId,
                        "/queue/messages", processedMessage.toDTO()
                ));
    }
}
