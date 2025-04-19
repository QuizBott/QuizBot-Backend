package mk.ukim.finki.quizbot.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mk.ukim.finki.quizbot.Model.DTO.Generate.QuizRecord;
import mk.ukim.finki.quizbot.Model.DTO.QuizCreateDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizCreateResponseDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizUpdateDTO;
import mk.ukim.finki.quizbot.Model.Quiz;
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

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/generate")
    @GetMapping
    public Page<Quiz> getQuizzes(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "6") Integer size,
                                           @RequestParam String category) {
        return quizService.getQuizzes(category, page, size);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(
            @PathVariable Long id,
            @RequestBody QuizUpdateDTO quizUpdateDTO
    ) {
        Quiz quiz = quizService.updateQuiz(id, quizUpdateDTO);
        return ResponseEntity.ok(quiz);
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
    @PostMapping(path = "/generate/gemini", consumes = { "multipart/form-data" })
    public ResponseEntity<QuizCreateResponseDTO> generateQuizV2(@RequestPart("quiz") String quizCreateDTO, @RequestPart("file") MultipartFile file) {
        try {
            QuizCreateDTO quizCreate = generateQuizCreateDTO(quizCreateDTO);
            Integer single = quizCreate.singleAnswerQuestions();
            Integer multi = quizCreate.multiAnswerQuestions();
            QuizRecord quizRecord = quizService.generateQuizGemini(single, multi, file);
            QuizCreateResponseDTO quizResponse = quizService.createQuizResponse(quizRecord, quizCreate);
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