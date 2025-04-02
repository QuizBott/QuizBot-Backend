package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    private List<Quiz> quizzes;

    public Tag(){}

    public Tag(String name, List<Quiz> quizzes) {
        this.name = name;
        this.quizzes = quizzes;
    }
}
