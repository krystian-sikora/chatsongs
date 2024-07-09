package pl.ksikora.chatsongs.playback.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDeviceRequest {
    @JsonProperty("device_id")
    private String deviceId;
    @JsonProperty("is_active")
    private Boolean isActive;
}
