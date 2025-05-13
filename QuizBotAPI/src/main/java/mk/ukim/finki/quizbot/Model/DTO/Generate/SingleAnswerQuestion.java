package mk.ukim.finki.quizbot.Model.DTO.Generate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record SingleAnswerQuestion(
        @JsonProperty(required = true, value = "question")
        @JsonPropertyDescription("The text of the single-answer question")
        String question,

        @JsonProperty(required = true, value = "points")
        @JsonPropertyDescription("The points awarded for answering the question correctly")
        Double points,

        @JsonProperty(required = true, value = "answers")
        @JsonPropertyDescription("Array of answer options for the single-answer question. Exactly one answer should be marked as correct.")
        AnswerRecord[] answers
) {
}