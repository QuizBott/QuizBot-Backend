package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
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
}
