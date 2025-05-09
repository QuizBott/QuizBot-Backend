package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuizAttemptDTO(
        Double points,
        String quizName,
        String quizTags,

        @JsonProperty(required = true, value = "image")
        String imageBase64
) {
}
