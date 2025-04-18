package mk.ukim.finki.quizbot.Service;

import mk.ukim.finki.quizbot.Model.Answer;
import mk.ukim.finki.quizbot.Repository.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;


    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;

    }


    public void deleteAnswer(Long id) {
        if (!answerRepository.existsById(id)) {
            throw new RuntimeException("Answer not found with id: " + id);
        }
        answerRepository.deleteById(id);
    }


    public List<Answer> findAll() {
        return answerRepository.findAll();
    }


    public void saveAnswer(Answer answer) {
        answerRepository.save(answer);
    }

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public Optional<Answer> findById(Long id) {
        return answerRepository.findById(id);
    }


    public void updateAnswer(Long id, Answer updatedAnswer) {
        Answer existing = answerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found"));

        existing.setQuestion(updatedAnswer.getQuestion());

        existing.setRightAnswer(updatedAnswer.getRightAnswer());

        answerRepository.save(existing);
    }
}
