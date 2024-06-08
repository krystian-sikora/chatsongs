package pl.ksikora.filmreviewerbackend.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatEntity, Long>{
    List<ChatEntity> findAllByUsersId(Long id);
}
