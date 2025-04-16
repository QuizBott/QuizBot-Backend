package mk.ukim.finki.quizbot.Service;

import mk.ukim.finki.quizbot.Model.Question;
import mk.ukim.finki.quizbot.Repository.AnswerRepository;
import mk.ukim.finki.quizbot.Repository.QuestionRepository;
import mk.ukim.finki.quizbot.Repository.QuizRepository;
import mk.ukim.finki.quizbot.Repository.TagRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class QuestionService {

    // Members
    private final ChatModel chatModel;
    private final String systemText;
    private final String userText;

    // Repo
    private final QuizRepository quizRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;


    public QuestionService(OllamaChatModel ollamaModel, QuizRepository quizRepository, TagRepository tagRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.chatModel = ollamaModel;
        this.quizRepository = quizRepository;
        this.tagRepository = tagRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.systemText = """
                You are a quiz generator. Given a context text, you must generate a quiz consisting of two types of questions:
                - SingleAnswerQuestions: For each of these, exactly 4 answers must be provided and exactly one of them marked as correct.
                - MultiAnswerQuestions: For each of these, exactly 4 answers must be provided, with more than one answer allowed to be marked as correct.

                The answers for each question must be extractable from the provided context text. Do not introduce any information that isn’t in the context.

                Ensure that for each SingleAnswerQuestion, only one Answer has "is_correct": true, and for each MultiAnswerQuestion, at least two Answers have "is_correct": true.
                """;

        this.userText = """
                Generate a quiz based on the following text:

                 "{context}"

                 Please generate {single} SingleAnswerQuestions and {multi} MultiAnswerQuestions.
                """;
    }


    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }


    public List<Question> findAll() {
        return questionRepository.findAll();
    }


    public void saveQuestion(Question question) {
        questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }


    public void updateQuestion(Long id, Question updatedQuestion) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        existing.setQuestion(updatedQuestion.getQuestion());
        existing.setPoints(updatedQuestion.getPoints());
        existing.setQuiz(updatedQuestion.getQuiz());
        existing.setAnswers(updatedQuestion.getAnswers());
        existing.setUserAnswers(updatedQuestion.getUserAnswers());

        questionRepository.save(existing);
    }
}