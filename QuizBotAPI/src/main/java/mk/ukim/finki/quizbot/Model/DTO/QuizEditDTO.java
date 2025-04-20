package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import mk.ukim.finki.quizbot.Model.DTO.Generate.MultiAnswerQuestion;
import mk.ukim.finki.quizbot.Model.DTO.Generate.SingleAnswerQuestion;

import java.util.List;


@Builder
@Getter
public class QuizEditDTO {
    @JsonProperty(required = true, value = "name")
    String name;

    @JsonProperty(value = "description")
    String description;

    @JsonProperty(required = true, value = "duration")
    Integer duration;

    @JsonProperty(required = true, value = "category")
    String category;

    @JsonProperty(required = true, value = "numberAttempts")
    Integer numberAttempts;

    @JsonProperty(required = true, value = "tags")
    List<TagDTO> tags;

    @JsonProperty(required = true, value = "single_answer_questions")
    SingleAnswerQuestion[] singleAnswerQuestions;

    @JsonProperty(required = true, value = "multi_answer_questions")
    MultiAnswerQuestion[] multiAnswerQuestions;


}
