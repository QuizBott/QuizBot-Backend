package mk.ukim.finki.quizbot.Controller;

import mk.ukim.finki.quizbot.Model.DTO.QuizRecord;
import mk.ukim.finki.quizbot.Service.QuizService;
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
    public QuizRecord generateQuiz(@RequestParam Integer single, @RequestParam Integer multi, @RequestBody MultipartFile file) {
        return quizService.generateQuiz(single, multi, file);
    }
}