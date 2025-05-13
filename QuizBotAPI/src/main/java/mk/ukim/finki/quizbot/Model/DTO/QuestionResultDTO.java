package mk.ukim.finki.quizbot.Model.DTO;

import java.util.List;

public record QuestionResultDTO(
        Long questionId,
        String questionText,
        Double maxPoints,
        Double earnedPoints,
        List<AnswerResultDTO> answers
) {}