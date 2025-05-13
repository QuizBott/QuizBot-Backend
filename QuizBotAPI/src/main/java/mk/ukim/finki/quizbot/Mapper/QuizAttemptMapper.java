package mk.ukim.finki.quizbot.Mapper;

import mk.ukim.finki.quizbot.Model.DTO.AnswerResultDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuestionResultDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizResultDTO;
import mk.ukim.finki.quizbot.Model.Question;
import mk.ukim.finki.quizbot.Model.QuizAttempt;
import mk.ukim.finki.quizbot.Model.UserAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QuizAttemptMapper {

    public QuizResultDTO mapToQuizResultDTO(QuizAttempt quizAttempt) {
        double earned = quizAttempt.getPoints();
        double max     = quizAttempt.getQuiz()
                .getQuestions()
                .stream()
                .mapToDouble(Question::getPoints)
                .sum();

        List<QuestionResultDTO> questionResults = groupByQuestion(quizAttempt)
                .entrySet().stream()
                .map(e -> mapToQuestionResult(e.getKey(), e.getValue()))
                .toList();

        return new QuizResultDTO(
                quizAttempt.getQuiz().getId(),
                quizAttempt.getQuiz().getName(),
                quizAttempt.getCreatedAt(),
                earned,
                max,
                questionResults
        );
    }

    private Map<Long, List<UserAnswer>> groupByQuestion(QuizAttempt quizAttempt) {
        return quizAttempt.getUserAnswers().stream()
                .collect(Collectors.groupingBy(ua -> ua.getQuestion().getId()));
    }

    private QuestionResultDTO mapToQuestionResult(Long questionId, List<UserAnswer> userAnswers) {
        Question question = userAnswers.getFirst().getQuestion();
        double earnedPoints = calculateEarnedPoints(question, userAnswers);
        List<AnswerResultDTO> answers = mapToAnswerResults(question, userAnswers);

        return new QuestionResultDTO(
                questionId,
                question.getQuestion(),
                question.getPoints(),
                earnedPoints,
                answers
        );
    }

    private double calculateEarnedPoints(Question question, List<UserAnswer> userAnswers) {
        return userAnswers.stream()
                .filter(ua -> ua.getAnswer().getIsCorrect())
                .mapToDouble(ua -> question.getPoints())
                .sum();
    }

    private List<AnswerResultDTO> mapToAnswerResults(Question question, List<UserAnswer> userAnswers) {
        return question.getAnswers().stream()
                .map(answer -> new AnswerResultDTO(
                        answer.getId(),
                        answer.getAnswer(),
                        userAnswers.stream()
                                .anyMatch(ua -> ua.getAnswer().getId().equals(answer.getId())),
                        answer.getIsCorrect()
                ))
                .collect(Collectors.toList());
    }
}

