package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizSimpleDTO(
        @JsonProperty(required = true, value = "id")
        Long id,

        @JsonProperty(required = true, value = "name")
        String name,

        @JsonProperty(value = "description")
        String description,

        @JsonProperty(required = true, value = "duration")
        Integer duration,

        @JsonProperty(required = true, value = "category")
        String category,

        @JsonProperty(required = true, value = "numberAttempts")
        Integer numberAttempts,

        @JsonProperty(required = true, value = "tags")
        List<String> tags,

        @JsonProperty(required = true, value = "numberOfQuestions")
        Integer numberOfQuestions,

        @JsonProperty(required = true, value = "image")
        String imageBase64
) {

}
