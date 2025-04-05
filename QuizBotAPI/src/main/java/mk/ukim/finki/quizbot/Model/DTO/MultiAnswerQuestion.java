package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record MultiAnswerQuestion(
        @JsonProperty(required = true, value = "multi_answer_question")
        @JsonPropertyDescription("The text of the multi-answer question")
        String question,

        @JsonProperty(required = true, value = "points")
        @JsonPropertyDescription("The points awarded for answering the question correctly")
        int points,

        @JsonProperty(required = true, value = "answers")
        @JsonPropertyDescription("Array of answer options for the multi-answer question. More than one answer can be correct.")
        Answer[] answers
) {
}