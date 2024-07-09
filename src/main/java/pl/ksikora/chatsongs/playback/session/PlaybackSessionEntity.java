package pl.ksikora.chatsongs.playback.session;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.chatsongs.chat.ChatEntity;
import pl.ksikora.chatsongs.playback.device.ClientDeviceEntity;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_playback_session")
public class PlaybackSessionEntity {
    @Id
    @GeneratedValue
    private Long sessionId;
    @OneToMany
    private List<ClientDeviceEntity> clientDevices;
    @OneToOne
    private ChatEntity chat;
}
