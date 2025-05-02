package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnswerDTO(
        @JsonProperty(required = true, value = "id")
        Long id,

        @JsonProperty(required = true, value = "isCorrect")
        Boolean isCorrect,

        @JsonProperty(required = true, value = "answer")
        String answer
) {
}
