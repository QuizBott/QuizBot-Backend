package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuizAttemptDTO(
        Long id,
        Long quizAttemptId,
        Double points,
        Double maxPoints,
        String quizName,
        String quizTags,

        @JsonProperty(required = true, value = "image")
        String imageBase64
) {
}
