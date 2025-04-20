package mk.ukim.finki.quizbot.Service;

import mk.ukim.finki.quizbot.Model.DTO.Generate.MultiAnswerQuestion;
import mk.ukim.finki.quizbot.Model.DTO.Generate.SingleAnswerQuestion;
import mk.ukim.finki.quizbot.Model.Question;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    private final AnswerService answerService;
    private final QuestionRepository questionRepository;

    public QuestionService(AnswerService answerService, QuestionRepository questionRepository) {
        this.answerService = answerService;
        this.questionRepository = questionRepository;
    }

    public List<Question> createQuestion(SingleAnswerQuestion[] single, MultiAnswerQuestion[] multi, Quiz quiz) {
        List<Question> questions = new ArrayList<>();
        for (SingleAnswerQuestion s : single) {
            Question question = new Question(s.question(), s.points(), quiz);
            question.setAnswers(answerService.createAnswer(s.answers(),question));
            questions.add(question);
        }

        for (MultiAnswerQuestion m : multi) {
            Question question = new Question(m.question(), m.points(), quiz);
            question.setAnswers(answerService.createAnswer(m.answers(),question));
            questions.add(question);
        }
        questionRepository.saveAll(questions);
        return questions;
    }
}
