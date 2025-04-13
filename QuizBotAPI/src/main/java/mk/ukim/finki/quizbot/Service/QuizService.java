package mk.ukim.finki.quizbot.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import mk.ukim.finki.quizbot.Model.DTO.QuizRecord;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Service
public class QuizService {

    private final ChatModel chatModel;
    private final String systemText;
    private final String userText;

    public QuizService(OllamaChatModel ollamaModel) {
        this.chatModel = ollamaModel;
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

    public QuizRecord generateQuiz(Integer single, Integer multi, MultipartFile file) {

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


    public QuizRecord generateQuizV2(Integer single, Integer multi, MultipartFile file) {
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