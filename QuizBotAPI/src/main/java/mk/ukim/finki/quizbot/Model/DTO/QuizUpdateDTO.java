package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.core.instrument.Tags;
import lombok.Data;
import lombok.Getter;
import mk.ukim.finki.quizbot.Model.Question;

import java.util.List;

public record QuizUpdateDTO(
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

//    @JsonProperty(value = "userId")
//    Long userId,

    @JsonProperty(required = true, value = "tags")
    List<TagDTO> tags,

    @JsonProperty(required = true, value = "questions")
    List<QuestionDTO> questions
) {
    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Integer duration() {
        return duration;
    }

    @Override
    public String category() {
        return category;
    }

    @Override
    public Integer numberAttempts() {
        return numberAttempts;
    }

    @Override
    public List<TagDTO> tags() {
        return tags;
    }

    @Override
    public List<QuestionDTO> questions() {
        return questions;
    }
}