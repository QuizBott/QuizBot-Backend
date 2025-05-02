package mk.ukim.finki.quizbot.Mapper;

import mk.ukim.finki.quizbot.Model.DTO.QuizDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizEditDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizSimpleDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizStartedDTO;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Model.Tag;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

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
                .image(Base64.getDecoder().decode(quiz.getImageBase64()))
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
                        .toList(),
                Base64.getEncoder().encodeToString(quiz.getImage())
        );
    }

    public QuizSimpleDTO toQuizSimpleDTO(Quiz quiz) {
        return new QuizSimpleDTO(
                quiz.getName(),
                quiz.getDescription(),
                quiz.getDuration(),
                quiz.getCategory(),
                quiz.getNumberAttempts(),
                quiz.getTags().stream()
                        .map(Tag::getName)
                        .toList(),
                Long.valueOf(quiz.getQuestions().size()).intValue(),
                Optional.ofNullable(quiz.getImage())
                        .map(Base64.getEncoder()::encodeToString)
                        .orElse(null)
        );
    }

    public QuizStartedDTO toQuizStartedDTO(Quiz quiz) {
        return new QuizStartedDTO(
                quiz.getName(),
                quiz.getDuration(),
                quiz.getQuestions().stream()
                        .map(questionMapper::toQuestionDTO)
                        .toList()
        );
    }

}
