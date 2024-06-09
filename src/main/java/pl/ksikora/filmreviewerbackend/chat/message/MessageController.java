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
    public void send(@Payload MessageDTO message, Principal user) {

        MessageEntity processedMessage = messageService.saveMessage(message, user.getName());

        template.convertAndSendToUser(
                processedMessage.getChat().getId().toString(),
                "/queue/messages", processedMessage
        );
    }
}
