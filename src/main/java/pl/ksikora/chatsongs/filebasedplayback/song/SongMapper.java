package pl.ksikora.chatsongs.filebasedplayback.song;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SongMapper {
    SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);

    @Mapping(source = "owner.id", target = "ownerId")
    SongResponse toDto(SongEntity entity);

    SongEntity toEntity(SongResponse response);
}
