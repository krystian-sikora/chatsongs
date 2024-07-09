package pl.ksikora.chatsongs.playback.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;

@RestControllerAdvice
public class SpotifyExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorizedException(UnauthorizedException e) {
        return e.getMessage();
    }
}
