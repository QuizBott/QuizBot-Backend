package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isSelected;

    @ManyToOne
    private Answer answer;

    @ManyToOne
    private Question question;

    @ManyToOne
    private QuizAttempt quizAttempt;

    public UserAnswer(){}

    public UserAnswer(Boolean isSelected, Answer answer, Question question, QuizAttempt quizAttempt) {
        this.isSelected = isSelected;
        this.answer = answer;
        this.question = question;
        this.quizAttempt = quizAttempt;
    }
}
