package mk.ukim.finki.quizbot.Mapper;

import mk.ukim.finki.quizbot.Model.DTO.QuizDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizEditDTO;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Model.Tag;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {

    private final QuestionMapper questionMapper;

    public QuizMapper(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    public Quiz toQuiz(QuizEditDTO quiz) {
        return Quiz.builder()
                .name(quiz.getName())
                .description(quiz.getDescription())
                .duration(quiz.getDuration())
                .category(quiz.getCategory())
                .numberAttempts(quiz.getNumberAttempts())
                .build();
    }

    public QuizDTO toQuizDTO(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getName(),
                quiz.getDescription(),
                quiz.getDuration(),
                quiz.getCategory(),
                quiz.getNumberAttempts(),
                quiz.getTags().stream()
                        .map(Tag::getName)
                        .toList(),
                quiz.getQuestions().stream()
                        .map(questionMapper::toQuestionDTO)
                        .toList()

        );
    }
}
