package mk.ukim.finki.quizbot.Controller;

import mk.ukim.finki.quizbot.Model.DTO.QuestionDTO;
import mk.ukim.finki.quizbot.Model.DTO.QuestionDTO1;
import mk.ukim.finki.quizbot.Model.Question;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Service.QuestionService;
import mk.ukim.finki.quizbot.Service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionWebController {
    private final QuestionService questionService;
    private final QuizService quizService;

    public QuestionWebController(QuestionService questionService, QuizService quizService) {
        this.questionService = questionService;
        this.quizService = quizService;

    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        QuestionDTO1 dto = new QuestionDTO1();
        dto.setAllQuizzes(quizService.getAllQuizzes());
        model.addAttribute("dto", dto);
        return "createQuestion";
    }

    @PostMapping("/create")
    public String createQuestion(@ModelAttribute QuestionDTO1 dto) {
        Quiz quiz = quizService.findById(dto.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid quiz ID: " + dto.getQuizId()));

        Question question = new Question();
        question.setQuestion(dto.getQuestion());
        question.setPoints(dto.getPoints());
        question.setQuiz(quiz);
        questionService.saveQuestion(question);
        return "redirect:/questions/create?success";
    }



    @GetMapping("/list")
    public String listAllQuestions(Model model) {
        List<Question> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "listQuestions"; // Return the template name
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + id));

        // Create and populate DTO
        QuestionDTO1 dto = new QuestionDTO1();
        dto.setId(question.getId());
        dto.setQuestion(question.getQuestion());
        dto.setPoints(question.getPoints());
        dto.setQuizId(question.getQuiz() != null ? question.getQuiz().getId() : null);
        dto.setAllQuizzes(quizService.getAllQuizzes());

        model.addAttribute("dto", dto);

        return "editQuestion";
    }

    @PostMapping("/{id}/edit")
    public String updateQuestion(@PathVariable Long id, @ModelAttribute QuestionDTO1 dto) {
        // Validate that the question exists
        Question existingQuestion = questionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + id));

        // Find the quiz by ID
        Quiz quiz = quizService.findById(dto.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid quiz ID: " + dto.getQuizId()));

        // Update the question with values from DTO
        existingQuestion.setQuestion(dto.getQuestion());
        existingQuestion.setPoints(dto.getPoints());
        existingQuestion.setQuiz(quiz);

        // Save the updated question
        questionService.saveQuestion(existingQuestion);

        return "redirect:/questions/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "redirect:/questions/list";
    }

}


