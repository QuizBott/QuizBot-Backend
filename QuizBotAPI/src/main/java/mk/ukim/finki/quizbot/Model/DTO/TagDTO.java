package mk.ukim.finki.quizbot.Model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ManyToMany;
import mk.ukim.finki.quizbot.Model.Quiz;

import java.util.List;

public record TagDTO(

        @JsonProperty(required = true, value = "id")
        Long id,

        @JsonProperty(required = true, value = "name")
        String name

//        @JsonProperty(value = "quizIds")
//        List<Long>quizIds
) {
        @Override
        public Long id() {
                return id;
        }

        @Override
        public String name() {
                return name;
        }
}
