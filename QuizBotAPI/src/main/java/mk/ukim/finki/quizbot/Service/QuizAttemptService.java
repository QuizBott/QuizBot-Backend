package mk.ukim.finki.quizbot.Service;

import jakarta.transaction.Transactional;
import mk.ukim.finki.quizbot.Mapper.QuizAttemptMapper;
import mk.ukim.finki.quizbot.Model.*;
import mk.ukim.finki.quizbot.Model.DTO.AnswerSubmitDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizResultDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizSubmitDTO;
import mk.ukim.finki.quizbot.Repository.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class QuizAttemptService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserContextService userContextService;

    private final QuizAttemptMapper quizAttemptMapper;

    public QuizAttemptService(QuizRepository quizRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, QuizAttemptRepository quizAttemptRepository, UserContextService userContextService, QuizAttemptMapper quizAttemptMapper) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userContextService = userContextService;
        this.quizAttemptMapper = quizAttemptMapper;
    }

    @Transactional
    public QuizAttempt submitQuiz(QuizSubmitDTO submission){

        Quiz quiz = quizRepository.findById(submission.quizId())
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
        ApplicationUser user = userContextService.getCurrentUser();

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setQuiz(quiz);
        quizAttempt.setUser(user);
        List<UserAnswer> userAnswers = new ArrayList<>();

        double totalPoints = 0;

        for (AnswerSubmitDTO a : submission.answers()) {
            Question question = questionRepository.findById(a.questionId())
                    .orElseThrow(() -> new NoSuchElementException("Question not found"));

            for (Long answerId : a.answerIds()) {
                Answer answer = answerRepository.findById(answerId)
                        .orElseThrow(() -> new NoSuchElementException("Answer not found"));

                UserAnswer userAnswer = new UserAnswer(answer, question, quizAttempt);
                userAnswers.add(userAnswer);

                if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                    totalPoints += question.getPoints();
                }
            }
        }

        quizAttempt.setPoints(totalPoints);
        quizAttempt.setUserAnswers(userAnswers);

        return quizAttemptRepository.save(quizAttempt);
    }

    public QuizResultDTO getLatestResult(Long quizId) {
        ApplicationUser user = userContextService.getCurrentUser();
        String username = user.getUsername();

        QuizAttempt quizAttempt = quizAttemptRepository
                .findByQuizIdAndUserUsername(quizId, username)
                .stream()
                .max(Comparator.comparing(QuizAttempt::getCreatedAt))
                .orElseThrow(() -> new NoSuchElementException("No attempt found"));

        return quizAttemptMapper.mapToQuizResultDTO(quizAttempt);
    }
}
