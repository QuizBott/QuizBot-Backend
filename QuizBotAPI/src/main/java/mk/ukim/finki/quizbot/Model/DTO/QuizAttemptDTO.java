package mk.ukim.finki.quizbot.Model.DTO;

public record QuizAttemptDTO(
        Double points,
        String quizName,
        String quizTags
) {
}
