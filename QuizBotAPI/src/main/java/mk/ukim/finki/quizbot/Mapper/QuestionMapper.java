package mk.ukim.finki.quizbot.Mapper;

import mk.ukim.finki.quizbot.Model.DTO.QuestionDTO;
import mk.ukim.finki.quizbot.Model.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    private final AnswerMapper answerMapper;

    public QuestionMapper(AnswerMapper answerMapper) {
        this.answerMapper = answerMapper;
    }

    public QuestionDTO toQuestionDTO(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getQuestion(),
                question.getType().toString(),
                question.getPoints(),
                question.getAnswers().stream()
                        .map(answerMapper::toAnswerDTO)
                        .toList()

        );
    }
}
