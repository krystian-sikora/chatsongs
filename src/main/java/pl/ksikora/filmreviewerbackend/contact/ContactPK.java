package pl.ksikora.filmreviewerbackend.contact;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ContactPK implements Serializable {
    protected Long userId;
    protected Long contactId;
}
