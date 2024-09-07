package pl.ksikora.chatsongs.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BadCredentialsException extends IllegalArgumentException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
