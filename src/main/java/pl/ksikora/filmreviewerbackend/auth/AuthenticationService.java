package pl.ksikora.filmreviewerbackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ksikora.filmreviewerbackend.config.JwtService;
import pl.ksikora.filmreviewerbackend.token.Token;
import pl.ksikora.filmreviewerbackend.token.TokenRepository;
import pl.ksikora.filmreviewerbackend.token.TokenType;
import pl.ksikora.filmreviewerbackend.user.UserRole;
import pl.ksikora.filmreviewerbackend.user.User;
import pl.ksikora.filmreviewerbackend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegistrationRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .nickname(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        userRepository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        revokeAllUserTokens(user);
        String accessToken = jwtService.generateToken(user);
        saveUserToken(user, accessToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }
}
