package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Setter
@Builder
@AllArgsConstructor
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

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    @OneToMany(mappedBy = "quiz")
    private List<QuizAttempt> quizAttempts;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] image;

    public Quiz() {
        questions = new ArrayList<>();
        quizAttempts = new ArrayList<>();
    }

    public Quiz(String name, String description, Integer duration, String category, Integer numberAttempts, Instant createdAt, ApplicationUser user, List<Tag> tags, List<Question> questions, List<QuizAttempt> quizAttempts, byte[] image) {
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
        this.image = image;
    }

}
