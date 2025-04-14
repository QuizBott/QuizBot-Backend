package mk.ukim.finki.quizbot.Repository;

import mk.ukim.finki.quizbot.Model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
