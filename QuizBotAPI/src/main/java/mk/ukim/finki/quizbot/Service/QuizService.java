package mk.ukim.finki.quizbot.Service;

import jakarta.transaction.Transactional;
import mk.ukim.finki.quizbot.Mapper.QuizMapper;
import mk.ukim.finki.quizbot.Model.*;
import mk.ukim.finki.quizbot.Model.DTO.*;
import mk.ukim.finki.quizbot.Model.DTO.Generate.QuizRecord;
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

    private final QuizMapper quizMapper;


    public QuizService(OllamaChatModel ollamaModel, UserContextService userContextService, QuestionService questionService, TagService tagService, QuizRepository quizRepository, TagRepository tagRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, UserRepository userRepository, QuizMapper quizMapper) {
        this.chatModel = ollamaModel;
        this.userContextService = userContextService;
        this.questionService = questionService;
        this.tagService = tagService;
        this.quizRepository = quizRepository;
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
    public QuizSimpleDTO getQuizIntro(Long id){
        Quiz quiz = quizRepository.getReferenceById(id);
        return quizMapper.toQuizSimpleDTO(quiz);
    }

    @Transactional
    public QuizStartedDTO getQuizStarted(Long id){
        Quiz quiz = quizRepository.getReferenceById(id);
        return quizMapper.toQuizStartedDTO(quiz);
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