package pl.ksikora.chatsongs.playback.device;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientDeviceMapper {

    ClientDeviceMapper INSTANCE = Mappers.getMapper(ClientDeviceMapper.class);

    @Mapping(source = "user.id", target = "userId")
    ClientDeviceResponse toDto(ClientDeviceEntity entity);
}