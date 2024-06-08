package pl.ksikora.filmreviewerbackend.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.ksikora.filmreviewerbackend.user.UserEntity;
import pl.ksikora.filmreviewerbackend.user.UserRepository;

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

