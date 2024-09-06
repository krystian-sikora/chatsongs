package pl.ksikora.chatsongs.filebasedplayback.queue;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QueueSongMapper {
    QueueSongMapper INSTANCE = Mappers.getMapper(QueueSongMapper.class);

    QueueSongResponse toResponse(QueueSongEntity entity);
    QueueSongEntity toEntity(QueueSongResponse response);
}
