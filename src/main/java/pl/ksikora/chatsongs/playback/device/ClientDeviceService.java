package pl.ksikora.chatsongs.playback.device;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ksikora.chatsongs.auth.AuthenticationFacade;
import pl.ksikora.chatsongs.user.UserEntity;

@Service
@AllArgsConstructor
public class ClientDeviceService {

    private final ClientDeviceRepository clientDeviceRepository;
    private final AuthenticationFacade authenticationFacade;

    public ClientDeviceResponse registerDevice(ClientDeviceRequest clientDevice) {
        UserEntity user = authenticationFacade.getCurrentUser();

        clientDeviceRepository.findByUser(user).ifPresentOrElse(
                device -> updateDevice(device, clientDevice),
                () -> createDevice(user, clientDevice)
        );

        return clientDeviceRepository.findByUser(user).map(ClientDeviceMapper.INSTANCE::toDto).orElseThrow();
    }

    private void createDevice(UserEntity user, ClientDeviceRequest clientDevice) {
        clientDeviceRepository.save(ClientDeviceEntity.builder()
                .deviceId(clientDevice.getDeviceId())
                .isActive(clientDevice.getIsActive())
                .user(user)
                .build());
    }

    private void updateDevice(ClientDeviceEntity device, ClientDeviceRequest clientDevice) {
//        The device_id is a primary key and cannot be changed,
//        so we need to delete the old device and create a new one
        device.setDeviceId(clientDevice.getDeviceId());
        device.setIsActive(clientDevice.getIsActive());
        clientDeviceRepository.save(device);
    }
}
