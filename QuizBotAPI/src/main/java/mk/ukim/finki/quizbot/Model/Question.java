package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Setter
@Getter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private Double points;

    @ManyToOne
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @OneToMany(mappedBy = "question")
    private List<UserAnswer> userAnswers;

    public Question(){
        answers = new ArrayList<>();
        userAnswers = new ArrayList<>();
    }

    public Question(String question, Double points, Quiz quiz) {
        this.question = question;
        this.points = points;
        this.quiz = quiz;
        this.answers = new ArrayList<>();
        this.userAnswers = new ArrayList<>();
    }

}
