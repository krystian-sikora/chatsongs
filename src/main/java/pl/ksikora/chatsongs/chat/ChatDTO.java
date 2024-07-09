package pl.ksikora.chatsongs.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.chatsongs.chat.message.MessageDTO;
import pl.ksikora.chatsongs.user.UserDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
    private Long id;
    private String name;
    private List<UserDTO> users;
    private List<MessageDTO> messages;
}
