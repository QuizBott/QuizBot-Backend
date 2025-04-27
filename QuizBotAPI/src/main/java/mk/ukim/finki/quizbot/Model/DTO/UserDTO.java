package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class UserDTO {
    @JsonProperty(required = true, value = "username")
    String username;
}
