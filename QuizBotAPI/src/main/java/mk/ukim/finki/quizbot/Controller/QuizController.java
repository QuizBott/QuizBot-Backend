package mk.ukim.finki.quizbot.Controller;

import mk.ukim.finki.quizbot.Model.DTO.Generate.QuizRecord;
import mk.ukim.finki.quizbot.Model.DTO.QuizCreateDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuizUpdateDTO;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/quiz")
public class QuizController {

    // Services
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

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

    @PostMapping("/generate/ollama")
    public ResponseEntity<QuizRecord> generateQuiz(@RequestParam Integer single, @RequestParam Integer multi, @RequestBody MultipartFile file) {
        try {
            QuizRecord quizRecord = quizService.generateQuizOllama(single, multi, file);
            return ResponseEntity.ok(quizRecord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/generate/gemini")
    public ResponseEntity<QuizRecord> generateQuizV2(@RequestPart("quiz") QuizCreateDTO quizCreateDTO, @RequestPart("file") MultipartFile file) {
        try {
            int single = quizCreateDTO.getSingleAnswerQuestions();
            int multi = quizCreateDTO.getMultiAnswerQuestions();
            QuizRecord quizRecord = quizService.generateQuizGemini(single, multi, file);

            return ResponseEntity.ok(quizRecord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}