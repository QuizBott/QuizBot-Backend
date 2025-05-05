package mk.ukim.finki.quizbot.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mk.ukim.finki.quizbot.Model.DTO.Generate.QuizRecord;
import mk.ukim.finki.quizbot.Model.DTO.*;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Model.QuizAttempt;
import mk.ukim.finki.quizbot.Service.QuizAttemptService;
import mk.ukim.finki.quizbot.Service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;

    public QuizController(QuizService quizService, QuizAttemptService quizAttemptService) {
        this.quizService = quizService;
        this.quizAttemptService = quizAttemptService;
    }

    @GetMapping
    public Page<QuizSimpleDTO> getQuizzes(@RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "6") Integer size,
                                 @RequestParam String category) {

        if (category == null || category.isBlank() || category.equalsIgnoreCase("All")) {
            return quizService.getAllQuizzes(page, size);
        }

        return quizService.getQuizzesByCategory(category, page, size);
    }

    @GetMapping("/{id}/intro")
    public ResponseEntity<QuizSimpleDTO> getQuizIntro(@PathVariable() Long id) {
        try{
            return ResponseEntity.ok(quizService.getQuizIntro(id));
        }
         catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizStartedDTO> getQuizStarted(@PathVariable() Long id) {
        try{
            return ResponseEntity.ok(quizService.getQuizStarted(id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmitDTO submission) {
        try {
            QuizAttempt quizAttempt =  quizAttemptService.submitQuiz(submission);
            return ResponseEntity.ok(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<QuizResultDTO> getResults(@PathVariable Long id) {
        try {
            QuizResultDTO dto = quizAttemptService.getLatestResult(id);
            return ResponseEntity.ok(dto);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<Boolean> getUserHasAttemptsForQuiz(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quizAttemptService.getUserHasAttemptsForQuiz(id));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/create")
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizEditDTO quizEditDTO) {
        try {
            QuizDTO quiz = quizService.createQuiz(quizEditDTO);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/generate/ollama")
    public ResponseEntity<QuizRecord> generateQuiz(@RequestParam Integer single, @RequestParam Integer multi, @RequestBody MultipartFile file) {
        try {
            QuizRecord quizRecord = quizService.generateQuizOllama(single, multi, file);
            return ResponseEntity.ok(quizRecord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping(path = "/generate/gemini", consumes = {"multipart/form-data"})
    public ResponseEntity<QuizEditDTO> generateQuizV2(@RequestPart("quiz") String quizCreateDTO, @RequestPart("file") MultipartFile file, @RequestPart("image") MultipartFile image) {
        try {
            QuizCreateDTO quizCreate = generateQuizCreateDTO(quizCreateDTO);
            Integer single = quizCreate.singleAnswerQuestions();
            Integer multi = quizCreate.multiAnswerQuestions();

            QuizRecord quizRecord = quizService.generateQuizGemini(single, multi, file);
            QuizEditDTO quizResponse = quizService.createQuizEditResponse(quizRecord, quizCreate, image);

            return ResponseEntity.ok(quizResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private QuizCreateDTO generateQuizCreateDTO(String quizCreateDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(quizCreateDTO, QuizCreateDTO.class);
    }


}