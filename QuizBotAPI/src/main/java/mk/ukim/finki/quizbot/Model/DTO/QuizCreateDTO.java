package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizCreateDTO(
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
        List<TagDTO> tags,
        @JsonProperty(required = true, value = "singleAnswerQuestions")
        Integer singleAnswerQuestions,
        @JsonProperty(required = true, value = "multiAnswerQuestions")
        Integer multiAnswerQuestions

) {

}
