package mk.ukim.finki.quizbot.Repository;

import mk.ukim.finki.quizbot.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
