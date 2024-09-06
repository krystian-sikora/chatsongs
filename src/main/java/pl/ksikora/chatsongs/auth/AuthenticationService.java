package pl.ksikora.chatsongs.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ksikora.chatsongs.config.JwtService;
import pl.ksikora.chatsongs.token.TokenEntity;
import pl.ksikora.chatsongs.token.TokenRepository;
import pl.ksikora.chatsongs.token.TokenType;
import pl.ksikora.chatsongs.user.UserDTO;
import pl.ksikora.chatsongs.user.UserRole;
import pl.ksikora.chatsongs.user.UserEntity;
import pl.ksikora.chatsongs.user.UserRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegistrationRequest request) {
        var user = UserEntity.builder()
                .email(request.getEmail())
                .nickname(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        userRepository.save(user);

        return getAuthenticationResponse(user);
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
        return getAuthenticationResponse(user);
    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        revokeAllUserTokens(user);

        var token = TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    private AuthenticationResponse getAuthenticationResponse(UserEntity user) {
        String accessToken = jwtService.generateToken(user);
        saveUserToken(user, accessToken);

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(jwtService.generateRefreshToken(user))
                .user(userDTO)
                .build();
    }

    public void refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader("AUTHORIZATION");
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractEmail(refreshToken);

        if (userEmail == null) return;

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtService.isTokenValid(refreshToken, user)) {
            String accessToken = jwtService.generateToken(user);
            saveUserToken(user, accessToken);

            var authResponse = AuthenticationResponse.builder()
                    .user(UserDTO.builder()
                            .id(user.getId())
                            .role(user.getRole())
                            .nickname(user.getNickname())
                            .email(user.getEmail())
                            .build())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }
}
