package pl.ksikora.chatsongs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FilmReviewerBackendApplicationTests {

    static {
        System.setProperty("JWT_SECRET_KEY", ":)");
        System.setProperty("FRONTEND_APP_URL", ":)");
        System.setProperty("SPOTIFY_CLIENT_ID", ":)");
        System.setProperty("SPOTIFY_CLIENT_SECRET", ":)");
    }

//    @Test
    void contextLoads() {
    }

}
