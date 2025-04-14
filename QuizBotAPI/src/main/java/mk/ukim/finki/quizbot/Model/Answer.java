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

    public Boolean getRightAnswer() {
        return isRightAnswer;
    }

    public Question getQuestion() {
        return question;
    }

    public List<UserAnswer> getUserAnswers() {
        return userAnswers;
    }

    public Long getId() {
        return id;
    }

    public void setRightAnswer(Boolean rightAnswer) {
        isRightAnswer = rightAnswer;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setUserAnswers(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }
}
