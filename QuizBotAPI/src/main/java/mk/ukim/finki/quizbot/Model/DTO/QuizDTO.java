package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizDTO(
        Long id,
        String name,
        String description,
        Integer duration,
        String category,
        Integer numberAttempts,
        List<String> tags,
        List<QuestionDTO> questions,
        @JsonProperty(required = true, value = "image")
        String imageBase64
) {
}
