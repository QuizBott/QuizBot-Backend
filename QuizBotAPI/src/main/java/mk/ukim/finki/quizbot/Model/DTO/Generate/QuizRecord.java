package mk.ukim.finki.quizbot.Model.DTO.Generate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record QuizRecord(

        @JsonProperty(required = true, value = "single_answer_questions")
        @JsonPropertyDescription("An array of single-answer questions, each with exactly one correct answer")
        SingleAnswerQuestion[] singleAnswerQuestions,

        @JsonProperty(required = true, value = "multi_answer_questions")
        @JsonPropertyDescription("An array of multi-answer questions, each with one or more correct answers")
        MultiAnswerQuestion[] multiAnswerQuestions
) {
}