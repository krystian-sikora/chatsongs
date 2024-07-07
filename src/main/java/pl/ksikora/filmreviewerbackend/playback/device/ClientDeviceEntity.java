package pl.ksikora.filmreviewerbackend.playback.device;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ksikora.filmreviewerbackend.user.UserEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_client_device")
public class ClientDeviceEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String deviceId;
    @OneToOne
    private UserEntity user;
    private Boolean isActive;
}
