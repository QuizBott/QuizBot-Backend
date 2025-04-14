package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import mk.ukim.finki.quizbot.Model.Answer;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Model.UserAnswer;

import java.util.List;

public record QuestionDTO(
        @JsonProperty(required = true, value = "id")
        Long id,

        @JsonProperty(required = true, value = "question")
        String question,

        @JsonProperty(required = true, value = "points")
        Double points,

        @JsonProperty(value = "quizId")
        Integer quizId,

        @JsonProperty(required = true, value = "answers")
        List<AnswerDTO> answers

//        @JsonProperty(value = "userAnswersIds")
//        List<Long> userAnswersIds
) {
        @Override
        public Long id() {
                return id;
        }

        @Override
        public String question() {
                return question;
        }

        @Override
        public Double points() {
                return points;
        }

        @Override
        public Integer quizId() {
                return quizId;
        }

        @Override
        public List<AnswerDTO> answers() {
                return answers;
        }
}
