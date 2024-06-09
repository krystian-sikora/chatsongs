package pl.ksikora.filmreviewerbackend.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.filmreviewerbackend.chat.message.MessageEntity;
import pl.ksikora.filmreviewerbackend.user.UserEntity;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_chat")
public class ChatEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Enumerated
    private ChatType type;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserEntity> users;
    @OneToMany(fetch = FetchType.EAGER)
    private List<MessageEntity> messages;

}
