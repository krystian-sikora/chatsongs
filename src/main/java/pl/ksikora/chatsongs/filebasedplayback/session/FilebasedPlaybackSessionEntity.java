package pl.ksikora.chatsongs.filebasedplayback.session;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.chatsongs.chat.ChatEntity;
import pl.ksikora.chatsongs.filebasedplayback.queue.QueueSongEntity;
import pl.ksikora.chatsongs.user.UserEntity;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_filebased_playback_session")
public class FilebasedPlaybackSessionEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ChatEntity chat;
    @OneToMany
    private List<UserEntity> activeUsers;
    @ManyToMany
    private List<QueueSongEntity> songQueue;
}
