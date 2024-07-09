package pl.ksikora.chatsongs.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.ksikora.chatsongs.user.UserEntity;
import pl.ksikora.chatsongs.user.UserRepository;

@Component
@AllArgsConstructor
public class AuthenticationFacade {

    public final UserRepository userRepository;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserEntity getCurrentUser() {
        return userRepository.findByEmail(getAuthentication().getName()).orElseThrow();
    }
}

