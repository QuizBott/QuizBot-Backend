package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isRightAnswer;

    @ManyToOne
    private Question question;

    @OneToMany(mappedBy = "answer")
    private List<UserAnswer> userAnswers;

    public Answer(){}

    public Answer(Boolean isRightAnswer, Question question, List<UserAnswer> userAnswers) {
        this.isRightAnswer = isRightAnswer;
        this.question = question;
        this.userAnswers = userAnswers;
    }
}
