package mk.ukim.finki.quizbot.Responses;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginResponse {
    private String token;

    private long expiresIn;
}
