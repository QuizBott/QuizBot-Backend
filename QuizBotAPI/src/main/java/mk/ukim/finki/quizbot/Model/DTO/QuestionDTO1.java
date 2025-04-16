package mk.ukim.finki.quizbot.Model.DTO;

import mk.ukim.finki.quizbot.Model.Quiz;

import java.util.List;

public class QuestionDTO1 {
    private Long id;

    private String question;

    private Double points;

    private Long quizId;

    private List<Quiz> allQuizzes;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public List<Quiz> getAllQuizzes() {
        return allQuizzes;
    }

    public void setAllQuizzes(List<Quiz> allQuizzes) {
        this.allQuizzes = allQuizzes;
    }



}
