package mk.ukim.finki.quizbot.Controller;

import mk.ukim.finki.quizbot.Model.Answer;
import mk.ukim.finki.quizbot.Model.DTO.AnswerDTO;
import mk.ukim.finki.quizbot.Model.DTO.AnswerDTO1;
import mk.ukim.finki.quizbot.Model.DTO.QuestionDTO1;
import mk.ukim.finki.quizbot.Model.Question;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Service.AnswerService;
import mk.ukim.finki.quizbot.Service.QuestionService;
import mk.ukim.finki.quizbot.Service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/answers")
public class AnswerWebController {
    private final QuestionService questionService;
    private final QuizService quizService;
    private final AnswerService answerService;


    public AnswerWebController(QuestionService questionService, QuizService quizService, AnswerService answerService) {
        this.questionService = questionService;
        this.quizService = quizService;
        this.answerService = answerService;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        AnswerDTO1 dto = new AnswerDTO1();
        dto.setQuestions(questionService.getAllQuestions());
        model.addAttribute("dto", dto);
        return "createAnswer";
    }

    @PostMapping("/create")
    public String createAnswer(@ModelAttribute AnswerDTO1 dto) {
        Question question = questionService.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid quiz ID: " + dto.getQuestionId()));

        Answer answer = new Answer();
        answer.setAnswerText(dto.getAnswerText());
        answer.setRightAnswer(dto.getIsRightAnswer());
        answer.setQuestion(question);
        answerService.saveAnswer(answer);
        return "redirect:/answers/create?success";
    }


    @GetMapping("/list")
    public String listAllAnswers(Model model) {
        List<Answer> answers = answerService.getAllAnswers();
        model.addAttribute("answers", answers);
        return "listAnswers"; // Return the template name
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Answer existingAnswer = answerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid answer ID: " + id));

        // Create and populate DTO
        AnswerDTO1 dto = new AnswerDTO1();
        dto.setId(existingAnswer.getId());
        dto.setQuestions(questionService.getAllQuestions());
        dto.setQuestionId(existingAnswer.getQuestion().getId());
        dto.setIsRightAnswer(existingAnswer.getRightAnswer());
        dto.setAnswerText(existingAnswer.getAnswerText());

        model.addAttribute("dto", dto);

        return "editAnswer";
    }

    @PostMapping("/{id}/edit")
    public String updateQuestion(@PathVariable Long id, @ModelAttribute AnswerDTO1 dto) {
        // Validate that the question exists
        Answer existingAnswer = answerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid answer ID: " + id));

        // Find the quiz by ID
        Question question = questionService.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + dto.getQuestionId()));

        // Update the question with values from DTO
        existingAnswer.setQuestion(question);
        existingAnswer.setRightAnswer(dto.getIsRightAnswer());
        existingAnswer.setAnswerText(dto.getAnswerText());
        // Save the updated question
        answerService.saveAnswer(existingAnswer);

        return "redirect:/answers/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return "redirect:/answers/list";
    }
}