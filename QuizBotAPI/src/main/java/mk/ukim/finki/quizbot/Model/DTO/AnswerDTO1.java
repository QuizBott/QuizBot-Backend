package mk.ukim.finki.quizbot.Model.DTO;

import mk.ukim.finki.quizbot.Model.Question;

import java.util.List;

public class AnswerDTO1 {
    private Long id;

    private Boolean isRightAnswer;
    private String answerText;
    private List<Question> questions;
    private Long questionId;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Boolean getIsRightAnswer() {
        return isRightAnswer;
    }

    public void setIsRightAnswer(Boolean rightAnswer) {
        isRightAnswer = rightAnswer;
    }
}
