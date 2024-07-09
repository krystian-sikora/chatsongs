package pl.ksikora.chatsongs.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.ksikora.chatsongs.user.exceptions.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDetails getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public List<UserDTO> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(UserEntity::toDTO)
                .toList();
    }
}
