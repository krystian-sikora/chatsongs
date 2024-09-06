package pl.ksikora.chatsongs.filebasedplayback.song;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
public class SongResponse {
    private UUID id;
    @JsonProperty("content_id")
    private UUID contentId;
    @JsonProperty("content_length")
    private Long contentLength;
    private String name;
    private Date created;
    @JsonProperty("owner_id")
    private Long ownerId;
}
