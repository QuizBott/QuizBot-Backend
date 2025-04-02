package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Data
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double points;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "quizAttempt")
    private List<UserAnswer> userAnswers;

    @ManyToOne
    private ApplicationUser user;

    @ManyToOne
    private Quiz quiz;

    public QuizAttempt(){}

    public QuizAttempt(Double points, Instant createdAt, List<UserAnswer> userAnswers, ApplicationUser user, Quiz quiz) {
        this.points = points;
        this.createdAt = createdAt;
        this.userAnswers = userAnswers;
        this.user = user;
        this.quiz = quiz;
    }
}
