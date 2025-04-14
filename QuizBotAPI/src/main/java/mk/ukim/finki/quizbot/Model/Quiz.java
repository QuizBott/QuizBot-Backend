package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Setter
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Integer duration;

    private String category;

    private Integer numberAttempts;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne
    private ApplicationUser user;

    @ManyToMany(mappedBy = "quizzes")
    private List<Tag> tags;

    @OneToMany(mappedBy = "quiz")
    private List<Question> questions;

    @OneToMany(mappedBy = "quiz")
    private List<QuizAttempt> quizAttempts;

    public Quiz() {}

    public Quiz(String name, String description, Integer duration, String category, Integer numberAttempts, Instant createdAt, ApplicationUser user, List<Tag> tags, List<Question> questions, List<QuizAttempt> quizAttempts) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.numberAttempts = numberAttempts;
        this.createdAt = createdAt;
        this.user = user;
        this.tags = tags;
        this.questions = questions;
        this.quizAttempts = quizAttempts;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getNumberAttempts() {
        return numberAttempts;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<QuizAttempt> getQuizAttempts() {
        return quizAttempts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNumberAttempts(Integer numberAttempts) {
        this.numberAttempts = numberAttempts;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setQuizAttempts(List<QuizAttempt> quizAttempts) {
        this.quizAttempts = quizAttempts;
    }
}
