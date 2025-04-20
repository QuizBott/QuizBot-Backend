package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TagDTO(

        @JsonProperty(required = true, value = "name")
        String name

) {
}
