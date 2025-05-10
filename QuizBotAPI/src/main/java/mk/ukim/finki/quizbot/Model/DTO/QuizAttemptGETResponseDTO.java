package mk.ukim.finki.quizbot.Model.DTO;

import mk.ukim.finki.quizbot.Model.QuizAttempt;

import java.util.List;

public record QuizAttemptGETResponseDTO(
        List<QuizAttemptDTO> quizAttemptDTOList,
        Integer quizzesTaken,
        Double quizzesAvg,
        Double quizzesMaxAvg,
        QuizAttemptDTO quizBestScore
) { }
