package pl.ksikora.chatsongs.playback.device;


import org.junit.jupiter.api.Test;
import pl.ksikora.chatsongs.user.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientDeviceMapperTest {
    @Test
    public void shouldMapToDto() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("email")
                .password("password")
                .build();

        ClientDeviceEntity device = ClientDeviceEntity.builder()
                .deviceId("deviceId")
                .isActive(true)
                .user(user)
                .build();

        ClientDeviceResponse dto = ClientDeviceMapper.INSTANCE.toDto(device);

        assertThat(dto).isNotNull();
        assertThat(dto.getDeviceId()).isEqualTo("deviceId");
        assertThat(dto.getIsActive()).isEqualTo(true);
        assertThat(dto.getUserId()).isEqualTo(1L);
    }
}
