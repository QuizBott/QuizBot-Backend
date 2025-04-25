package mk.ukim.finki.quizbot.Service;

import jakarta.transaction.Transactional;
import mk.ukim.finki.quizbot.Mapper.QuizMapper;
import mk.ukim.finki.quizbot.Model.*;
import mk.ukim.finki.quizbot.Model.DTO.Generate.QuizRecord;
import mk.ukim.finki.quizbot.Model.DTO.QuizCreateDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizEditDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizUpdateDTO;
import mk.ukim.finki.quizbot.Repository.*;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Service
public class QuizService {

    private final ChatModel chatModel;
    private final String systemText;
    private final String userText;

    private final UserContextService userContextService;
    private final QuestionService questionService;
    private final TagService tagService;

    private final QuizRepository quizRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    private final QuizMapper quizMapper;


    public QuizService(OllamaChatModel ollamaModel, UserContextService userContextService, QuestionService questionService, TagService tagService, QuizRepository quizRepository, TagRepository tagRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, UserRepository userRepository, QuizMapper quizMapper) {
        this.chatModel = ollamaModel;
        this.userContextService = userContextService;
        this.questionService = questionService;
        this.tagService = tagService;
        this.quizRepository = quizRepository;
        this.tagRepository = tagRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.quizMapper = quizMapper;
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

    public QuizEditDTO createQuizEditResponse(QuizRecord quizRecord, QuizCreateDTO quizCreate, MultipartFile image)
    {
        try{
            QuizEditDTO quizResponse = QuizEditDTO.builder()
                    .name(quizCreate.name())
                    .description(quizCreate.description())
                    .duration(quizCreate.duration())
                    .category(quizCreate.category())
                    .numberAttempts(quizCreate.numberAttempts())
                    .tags(quizCreate.tags())
                    .singleAnswerQuestions(quizRecord.singleAnswerQuestions())
                    .multiAnswerQuestions(quizRecord.multiAnswerQuestions())
                    .imageBase64(Base64.getEncoder().encodeToString(image.getBytes()))
                    .build();

            return quizResponse;
        }catch (IOException e){
            throw new RuntimeException("Failed to convert image to byte array", e);
        }

    }

    @Transactional
    public QuizDTO createQuiz(QuizEditDTO quizEditDTO) {

        ApplicationUser user = userContextService.getCurrentUser();
        Quiz quiz = quizMapper.toQuiz(quizEditDTO);
        quiz.setUser(user);

        quizRepository.save(quiz);

        List<Tag> tags = tagService.getOrCreateTags(quizEditDTO.getTags(), quiz);
        quiz.setTags(tags);
        List<Question> questions = questionService.createQuestion(quizEditDTO.getSingleAnswerQuestions(), quizEditDTO.getMultiAnswerQuestions(), quiz);
        quiz.setQuestions(questions);

        quizRepository.save(quiz);

        return quizMapper.toQuizDTO(quiz);

    }


    /*@Transactional
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
                                    if (dtoAnswer.isCorrect() != null &&
                                            !dtoAnswer.isCorrect().equals(answer.getIsCorrect())) {
                                        answer.setIsCorrect(dtoAnswer.isCorrect());
                                    }
                                    return answer;
                                }).toList();

                                // Save all updated answers
                                answerRepository.saveAll(answers);
                                question.setAnswers(answers);
                                change = true;
                            }

                            if (change) {
                                questionRepository.save(question); // persist the change
                            }
                            return question;
                        }).toList();
                quiz.setQuestions(questions);
            }

            // update other fields if needed
            return quizRepository.save(quiz);
        }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }*/

    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
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

            Map<String, Object> requestPayload = Map.of(
                    "context", context,
                    "single", single,
                    "multi", multi,
                    "systemText", systemText,
                    "userText", userText
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestPayload, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<QuizRecord> response = restTemplate.postForEntity(
                    "http://localhost:8000/generate", request, QuizRecord.class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to call quiz generator API", e);
        }
    }
}