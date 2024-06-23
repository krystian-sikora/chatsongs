package pl.ksikora.filmreviewerbackend.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
    @Query("SELECT c FROM ContactEntity c WHERE c.id.userId = :userId OR c.id.contactId = :userId")
    List<ContactEntity> getAllById(Long userId);
}
