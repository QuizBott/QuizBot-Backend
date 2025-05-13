package mk.ukim.finki.quizbot.Model.DTO;

import java.time.Instant;
import java.util.List;

public record QuizResultDTO(
        Long quizId,
        String quizName,
        Instant takenAt,
        Double totalPoints,
        Double maxPoints,
        List<QuestionResultDTO> questions
) {}
