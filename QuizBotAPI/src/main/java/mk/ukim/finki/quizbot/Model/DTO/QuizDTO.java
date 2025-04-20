package mk.ukim.finki.quizbot.Model.DTO;

import java.util.List;

public record QuizDTO(
        Long id,
        String name,
        String description,
        Integer duration,
        String category,
        Integer numberAttempts,
        List<String> tags,
        List<QuestionDTO> questions
) {
}
