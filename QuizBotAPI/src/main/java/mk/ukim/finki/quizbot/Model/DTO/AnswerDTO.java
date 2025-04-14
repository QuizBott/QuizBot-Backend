package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import mk.ukim.finki.quizbot.Model.Question;
import mk.ukim.finki.quizbot.Model.UserAnswer;

import java.util.List;

public record AnswerDTO(
        @JsonProperty(required = true, value = "id")
        Long id,

        @JsonProperty(required = true, value = "isRightAnswer")
        Boolean isRightAnswer

//        @JsonProperty(required = true, value = "questionId")
//        Long questionId

//        @JsonProperty(value = "userAnswersIds")
//        List<Long> userAnswersIds
) {
    @Override
    public Long id() {
        return id;
    }

    @Override
    public Boolean isRightAnswer() {
        return isRightAnswer;
    }
}
