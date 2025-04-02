package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private Double points;

    @ManyToOne
    private Quiz quiz;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers;

    @OneToMany(mappedBy = "question")
    private List<UserAnswer> userAnswers;

    public Question(){}

    public Question(String question, Double points, Quiz quiz, List<Answer> answers, List<UserAnswer> userAnswers) {
        this.question = question;
        this.points = points;
        this.quiz = quiz;
        this.answers = answers;
        this.userAnswers = userAnswers;
    }
}
