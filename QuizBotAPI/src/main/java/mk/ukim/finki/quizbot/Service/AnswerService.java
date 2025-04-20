package mk.ukim.finki.quizbot.Service;

import mk.ukim.finki.quizbot.Model.Answer;
import mk.ukim.finki.quizbot.Model.DTO.Generate.AnswerRecord;
import mk.ukim.finki.quizbot.Model.Question;
import mk.ukim.finki.quizbot.Repository.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public List<Answer> createAnswer(AnswerRecord[] answerRecord, Question question) {
        List<Answer> answers = new ArrayList<>();

        for (AnswerRecord a : answerRecord) {
            Answer answer = new Answer(a.isCorrect(), question);
            answers.add(answer);
        }
        answerRepository.saveAll(answers);
        return answers;
    }

}
