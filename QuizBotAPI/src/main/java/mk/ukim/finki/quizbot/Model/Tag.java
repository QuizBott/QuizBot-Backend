package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany
    private List<Quiz> quizzes;

    public Tag() {
        quizzes = new ArrayList<>();
    }

    public Tag(String name) {
        this.name = name;
        this.quizzes = new ArrayList<>();
    }

    public void addQuiz(Quiz quiz) {
        this.quizzes.add(quiz);
    }

}
