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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }
}
