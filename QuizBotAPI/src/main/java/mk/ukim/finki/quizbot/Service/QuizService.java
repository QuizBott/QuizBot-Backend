package mk.ukim.finki.quizbot.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import mk.ukim.finki.quizbot.Model.Answer;
import mk.ukim.finki.quizbot.Model.DTO.Generate.QuizRecord;
import mk.ukim.finki.quizbot.Model.DTO.QuizUpdateDTO;
import mk.ukim.finki.quizbot.Model.Question;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Model.Tag;
import mk.ukim.finki.quizbot.Repository.AnswerRepository;
import mk.ukim.finki.quizbot.Repository.QuestionRepository;
import mk.ukim.finki.quizbot.Repository.QuizRepository;
import mk.ukim.finki.quizbot.Repository.TagRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class QuizService {

    // Members
    private final ChatModel chatModel;
    private final String systemText;
    private final String userText;

    // Repo
    private final QuizRepository quizRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;


    public QuizService(OllamaChatModel ollamaModel, QuizRepository quizRepository, TagRepository tagRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
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

    public Page<Quiz> getQuizzes(String category, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return quizRepository.findByCategoryIgnoreCase(category, pageable);
    }

    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }

    @Transactional
    public Quiz updateQuiz(Long id, QuizUpdateDTO quizDTO) {
        return quizRepository.findById(id).map(quiz -> {
            quiz.setName(quizDTO.name());
            quiz.setCategory(quizDTO.category());
            quiz.setDescription(quizDTO.description());
            quiz.setDuration(quizDTO.duration());
            quiz.setNumberAttempts(quizDTO.numberAttempts());

            // Update Tags
            if (quizDTO.tags() != null) {
                List<Tag> tags = quizDTO.tags().stream()
                        .map(dtoTag -> {
                                Tag tag = tagRepository.findById(dtoTag.id()).orElseThrow(() -> new RuntimeException("Tag not found with id: " + dtoTag.id()));
                                if (dtoTag.name() != null && !dtoTag.name().equals(tag.getName())) {
                                    tag.setName(dtoTag.name());
                                }
                                return tag;
                        }).toList();
                tagRepository.saveAll(tags); // persist the change
                quiz.setTags(tags);
            }

            // Update Questions
            if (quizDTO.questions() != null) {
                List<Question> questions = quizDTO.questions().stream()
                        .map(dtoQuestion -> {
                            boolean change = false;
                            Question question = questionRepository.findById(dtoQuestion.id()).orElseThrow(() -> new RuntimeException("Question not found with id: " + dtoQuestion.id()));

                            if (dtoQuestion.question() != null && !dtoQuestion.question().equals(question.getQuestion())) {
                                question.setQuestion(dtoQuestion.question());
                                change = true;
                            }

                            if (dtoQuestion.points() != null && !dtoQuestion.points().equals(question.getPoints())) {
                                question.setPoints(dtoQuestion.points());
                                change = true;
                            }

                            if (dtoQuestion.answers() != null && !dtoQuestion.answers().isEmpty()) {
                                List<Answer> answers = dtoQuestion.answers().stream().map(dtoAnswer -> {
                                    Answer answer = answerRepository.findById(dtoAnswer.id())
                                            .orElseThrow(() -> new RuntimeException("Answer not found with id: " + dtoAnswer.id()));
                                    if (dtoAnswer.isRightAnswer() != null &&
                                            !dtoAnswer.isRightAnswer().equals(answer.getRightAnswer())) {
                                        answer.setRightAnswer(dtoAnswer.isRightAnswer());
                                    }
                                    return answer;
                                }).toList();

                                // Save all updated answers
                                answerRepository.saveAll(answers);
                                question.setAnswers(answers);
                                change = true;
                            }

                            if(change){
                                questionRepository.save(question); // persist the change
                            }
                            return question;
                        }).toList();
                quiz.setQuestions(questions);
            }

            // update other fields if needed
            return quizRepository.save(quiz);
        }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    private List<Message> createPrompt(Integer single, Integer multi, String context) {

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage();

        PromptTemplate userPromptTemplate = new PromptTemplate(userText);

        Message userMessage = userPromptTemplate.createMessage(Map.of("context", context, "single", single, "multi", multi));

        return List.of(systemMessage, userMessage);
    }


    private String getContent(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    public QuizRecord generateQuizOllama(Integer single, Integer multi, MultipartFile file) {

        String context = this.getContent(file);

        BeanOutputConverter<QuizRecord> outputConverter = new BeanOutputConverter<>(QuizRecord.class);
        try {

            Prompt prompt = new Prompt(this.createPrompt(single, multi, context),
                    OllamaOptions.builder()
                            .format(outputConverter.getJsonSchemaMap())
                            .build());

            ChatResponse response = chatModel.call(prompt);
            String content = response.getResult().getOutput().getText();

            return outputConverter.convert(content);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate quiz with OllamaChatModel", e);
        }
    }


    public QuizRecord generateQuizGemini(Integer single, Integer multi, MultipartFile file) {
        try {
            String context = this.getContent(file);

            ProcessBuilder builder = new ProcessBuilder(
                    "python", "scripts/quiz_generator.py",
                    "--context", context,
                    "--single", String.valueOf(single),
                    "--multi", String.valueOf(multi),
                    "--systemText", systemText,
                    "--userText", userText
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Python process exited with code " + exitCode);
                }

                String jsonOutput = output.toString();

                ObjectMapper mapper = new ObjectMapper();
                Quiz quiz = new Quiz();
                QuizRecord qr = mapper.readValue(jsonOutput, QuizRecord.class);
                quiz.setName(qr.name());


                return mapper.readValue(jsonOutput, QuizRecord.class);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Process was interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate quiz via Python process", e);
        }
    }
}