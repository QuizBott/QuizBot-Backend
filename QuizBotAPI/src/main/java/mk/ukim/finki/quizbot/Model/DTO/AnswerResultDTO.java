package mk.ukim.finki.quizbot.Model.DTO;

public record AnswerResultDTO(
        Long answerId,
        String answerText,
        Boolean selected,
        Boolean correct
) {}