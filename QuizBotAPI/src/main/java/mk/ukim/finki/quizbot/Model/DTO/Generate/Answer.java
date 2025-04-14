package mk.ukim.finki.quizbot.Model.DTO.Generate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record Answer(
        @JsonProperty(required = true, value = "answer")
        @JsonPropertyDescription("The text of the answer option")
        String answer,

        @JsonProperty(required = true, value = "is_correct")
        @JsonPropertyDescription("A boolean flag indicating whether this answer is correct")
        Boolean isCorrect
) {
}