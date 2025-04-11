package mk.ukim.finki.quizbot.Controller;

import mk.ukim.finki.quizbot.Model.DTO.QuizRecord;
import mk.ukim.finki.quizbot.Service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/generate")
    public ResponseEntity<QuizRecord> generateQuiz(@RequestParam Integer single, @RequestParam Integer multi, @RequestBody MultipartFile file) {
        try {
            QuizRecord quizRecord = quizService.generateQuiz(single, multi, file);
            return ResponseEntity.ok(quizRecord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/generate/v2")
    public ResponseEntity<QuizRecord> generateQuizV2(@RequestParam Integer single, @RequestParam Integer multi, @RequestBody MultipartFile file) {
        try {
            QuizRecord quizRecord = quizService.generateQuizV2(single, multi, file);
            return ResponseEntity.ok(quizRecord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}