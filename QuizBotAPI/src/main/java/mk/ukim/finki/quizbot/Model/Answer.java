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
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isCorrect;

    private String answer;

    @ManyToOne
    private Question question;

    @OneToMany(mappedBy = "answer")
    private List<UserAnswer> userAnswers;

    public Answer(){
        userAnswers = new ArrayList<>();
    }

    public Answer(Boolean isCorrect, Question question, String answer) {
        this.isCorrect = isCorrect;
        this.question = question;
        this.userAnswers = new ArrayList<>();
        this.answer = answer;
    }

}
