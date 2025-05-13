package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizSubmitDTO(
        @JsonProperty(value = "quizId", required = true)
        Long quizId,

        @JsonProperty(value = "answers", required = true)
        List<AnswerSubmitDTO> answers
) {

}