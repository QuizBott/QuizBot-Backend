package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuestionDTO(
        @JsonProperty(required = true, value = "id")
        Long id,

        @JsonProperty(required = true, value = "question")
        String question,

        @JsonProperty(required = true, value = "points")
        Double points,

        @JsonProperty(required = true, value = "answers")
        List<AnswerDTO> answers

//        @JsonProperty(value = "userAnswersIds")
//        List<Long> userAnswersIds
) {

}
