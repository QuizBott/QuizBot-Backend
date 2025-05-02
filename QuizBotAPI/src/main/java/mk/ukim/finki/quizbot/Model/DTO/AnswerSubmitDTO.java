package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnswerSubmitDTO(
        @JsonProperty(value = "questionId", required = true)
        Long questionId,

        @JsonProperty(value = "answerIds", required = true)
        List<Long> answerIds

) {}
