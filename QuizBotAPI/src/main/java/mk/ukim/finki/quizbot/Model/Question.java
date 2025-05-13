package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.quizbot.Model.Enum.QuestionTypes;
import mk.ukim.finki.quizbot.Model.Enum.UserRoles;

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

    @Enumerated(EnumType.STRING)
    private QuestionTypes type;

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

    public Question(String question, Double points, QuestionTypes type,Quiz quiz) {
        this.question = question;
        this.points = points;
        this.type = type;
        this.quiz = quiz;
        this.answers = new ArrayList<>();
        this.userAnswers = new ArrayList<>();
    }

}
