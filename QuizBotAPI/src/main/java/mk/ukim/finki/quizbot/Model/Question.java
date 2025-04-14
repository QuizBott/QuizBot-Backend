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

    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public Double getPoints() {
        return points;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public List<UserAnswer> getUserAnswers() {
        return userAnswers;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void setUserAnswers(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }
}
