package mk.ukim.finki.quizbot.Mapper;

import mk.ukim.finki.quizbot.Model.Answer;
import mk.ukim.finki.quizbot.Model.DTO.AnswerDTO;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {

    public AnswerDTO toAnswerDTO(Answer answer) {
        return new AnswerDTO(
                answer.getId(),
                answer.getIsCorrect(),
                answer.getAnswer()
        );
    }
}
