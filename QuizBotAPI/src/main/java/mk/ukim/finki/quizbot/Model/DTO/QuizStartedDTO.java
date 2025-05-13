package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizStartedDTO(
    @JsonProperty(required = true, value = "name")
    String name,

    @JsonProperty(required = true, value = "duration")
    Integer duration,

    @JsonProperty(required = true, value = "questions")
    List<QuestionDTO> questions
) {
}