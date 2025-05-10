package mk.ukim.finki.quizbot.Service;

import jakarta.transaction.Transactional;
import mk.ukim.finki.quizbot.Mapper.QuizAttemptMapper;
import mk.ukim.finki.quizbot.Model.*;
import mk.ukim.finki.quizbot.Model.DTO.*;
import mk.ukim.finki.quizbot.Repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    public QuizAttempt submitQuiz(QuizSubmitDTO submission) {

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

            Set<Long> submittedAnswerIds = new HashSet<>(a.answerIds());

            List<Answer> allAnswers = answerRepository.findAllByQuestionId(question.getId());

            Set<Long> correctAnswerIds = allAnswers.stream()
                    .filter(Answer::getIsCorrect)
                    .map(Answer::getId)
                    .collect(Collectors.toSet());

            Set<Long> incorrectAnswerIds = allAnswers.stream()
                    .filter(ans -> !ans.getIsCorrect())
                    .map(Answer::getId)
                    .collect(Collectors.toSet());

            for (Long answerId : submittedAnswerIds) {
                Answer answer = answerRepository.findById(answerId)
                        .orElseThrow(() -> new NoSuchElementException("Answer not found"));
                userAnswers.add(new UserAnswer(answer, question, quizAttempt));
            }

            long correctSelected = submittedAnswerIds.stream()
                    .filter(correctAnswerIds::contains)
                    .count();

            long incorrectSelected = submittedAnswerIds.stream()
                    .filter(incorrectAnswerIds::contains)
                    .count();

            double points = 0.0;
            if (!correctAnswerIds.isEmpty()) {
                points += question.getPoints() * ((double) correctSelected / correctAnswerIds.size());
            }
            if (!allAnswers.isEmpty()) {
                points -= question.getPoints() * ((double) incorrectSelected / allAnswers.size());
            }

            totalPoints += points;
        }

        quizAttempt.setPoints(totalPoints);
        quizAttempt.setUserAnswers(userAnswers);

        return quizAttemptRepository.save(quizAttempt);
    }

    public QuizResultDTO getLatestResult(Long quizId) {
        ApplicationUser user = userContextService.getCurrentUser();
        String username = user.getUsername();

        QuizAttempt quizAttempt = quizAttemptRepository
                .findByQuizIdAndUserEmail(quizId, username)
                .stream()
                .max(Comparator.comparing(QuizAttempt::getCreatedAt))
                .orElseThrow(() -> new NoSuchElementException("No attempt found"));

        return quizAttemptMapper.mapToQuizResultDTO(quizAttempt);
    }

    public Boolean getUserHasAttemptsForQuiz(Long quizId) {
        ApplicationUser user = userContextService.getCurrentUser();
        String username = user.getUsername();

        Quiz quiz = quizRepository.findById(quizId).get();

        int quizAttemptsCount = quizAttemptRepository
                .findByQuizIdAndUserEmail(quizId, username).size();

        return quizAttemptsCount < quiz.getNumberAttempts();
    }

    @Transactional
    public QuizAttemptGETResponseDTO getQuizzesAttempted() {
        ApplicationUser user = userContextService.getCurrentUser();

        List<QuizAttempt> quizAttempted = quizAttemptRepository.findAllByUserEmail(user.getUsername());

        List<QuizAttemptDTO> quizAttemptDTOList = quizAttempted.stream()
                .map(quizAttempt -> new QuizAttemptDTO(
                        quizAttempt.getQuiz().getId(),
                        quizAttempt.getPoints(),
                        quizAttempt.getQuiz().getMaxPoints(),
                        quizAttempt.getQuiz().getName(),
                        quizAttempt.getQuiz().getTags().stream()
                                .map(Tag::getName)
                                .collect(Collectors.joining(", ")),
                        Base64.getEncoder().encodeToString(quizAttempt.getQuiz().getImage())
                ))
                .collect(Collectors.toList());

        double avg = quizAttempted.stream()
                .mapToDouble(QuizAttempt::getPoints)
                .average()
                .orElse(0.0);

        double maxAvg = quizAttempted.stream()
                .mapToDouble(q -> q.getQuiz().getMaxPoints())
                .average()
                .orElse(0.0);

        Optional<QuizAttempt> highestAttemptOpt = quizAttempted.stream()
                .max(Comparator.comparingDouble(QuizAttempt::getPoints));

        QuizAttemptDTO bestAttemptDTO = highestAttemptOpt.map(bestAttempt -> new QuizAttemptDTO(
                bestAttempt.getQuiz().getId(),
                bestAttempt.getPoints(),
                bestAttempt.getQuiz().getMaxPoints(),
                bestAttempt.getQuiz().getName(),
                bestAttempt.getQuiz().getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining(", ")),
                Base64.getEncoder().encodeToString(bestAttempt.getQuiz().getImage())
        )).orElse(null);

        return new QuizAttemptGETResponseDTO(
                quizAttemptDTOList,
                quizAttempted.size(),
                avg,
                maxAvg,
                bestAttemptDTO);
    }

    @Transactional
    public QuizResultDTO getQuizAttempt(Long id){
        Optional<QuizAttempt> quizAttempt = quizAttemptRepository.findById(id);

        if (quizAttempt.isPresent()) {
            return quizAttemptMapper.mapToQuizResultDTO(quizAttempt.get());
        }

        throw new NoSuchElementException("No attempt found");

    }

}
