package pl.ksikora.filmreviewerbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<List<UserEntity>> findAllByIdIn(List<Long> ids);

    boolean existsByEmail(String email);
}
