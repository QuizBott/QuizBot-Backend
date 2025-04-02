package mk.ukim.finki.quizbot.Model;

import jakarta.persistence.*;
import lombok.Data;
import mk.ukim.finki.quizbot.Model.Enum.UserRoles;

import java.util.List;
import java.util.UUID;

@Entity
@Data
public class ApplicationUser{

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING) // Store enum as a string
    private UserRoles userRoles;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Quiz> quizzes;

    @OneToMany(mappedBy = "user")
    private List<QuizAttempt> quizAttempts;

    public ApplicationUser() {}

    public ApplicationUser(String firstName, String lastName, UserRoles userRoles, String email, String password, List<Quiz> quizzes, List<QuizAttempt> quizAttempts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRoles = userRoles;
        this.email = email;
        this.password = password;
        this.quizzes = quizzes;
        this.quizAttempts = quizAttempts;
    }
}
