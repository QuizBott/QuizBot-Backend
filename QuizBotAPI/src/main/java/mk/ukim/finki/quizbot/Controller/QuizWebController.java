package mk.ukim.finki.quizbot.Controller;

import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/quizzes")
public class QuizWebController {

    private final QuizService quizService;

    public QuizWebController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("quiz", new Quiz());
        return "createQuiz"; // maps to createQuiz.html
    }

    @PostMapping("/create")
    public String createQuiz(@ModelAttribute Quiz quiz) {
        // NOTE: You'll probably need to handle default values, tags, etc.
        quizService.saveQuiz(quiz); // <-- you'll need this method
        return "redirect:/quizzes/create?success";
    }

    @GetMapping("/list")
    public String listAllQuizzes(Model model) {
        List<Quiz> quizzes = quizService.getAllQuizzes(); // Fetch all quizzes from the service
        model.addAttribute("quizzes", quizzes); // Add quizzes to the model
        return "listQuizzes"; // Return the template name
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quiz ID: " + id));
        model.addAttribute("quiz", quiz);
        return "editQuiz"; // Your edit form template
    }

    @PostMapping("/{id}/edit")
    public String updateQuiz(@PathVariable Long id, @ModelAttribute Quiz updatedQuiz) {
        quizService.updateQuiz(id, updatedQuiz); // We'll define this in the service
        return "redirect:/quizzes/list";
    }


    @PostMapping("/{id}/delete")
    public String deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return "redirect:/quizzes/list";
    }

}