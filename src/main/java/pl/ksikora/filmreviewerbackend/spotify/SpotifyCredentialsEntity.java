package pl.ksikora.filmreviewerbackend.spotify;

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
@Table(name = "_spotify_credentials")
public class SpotifyCredentialsEntity {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private UserEntity user;
    private String access_token;
    private String refresh_token;
}
