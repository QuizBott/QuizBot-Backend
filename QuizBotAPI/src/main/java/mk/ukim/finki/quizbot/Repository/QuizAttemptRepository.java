package mk.ukim.finki.quizbot.Repository;

import mk.ukim.finki.quizbot.Model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByQuizIdAndUserEmail(Long quizId, String username);

    List<QuizAttempt> findAllByUserEmail(String username);
}
