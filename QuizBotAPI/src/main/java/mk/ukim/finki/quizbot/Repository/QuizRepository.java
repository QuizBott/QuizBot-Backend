package mk.ukim.finki.quizbot.Repository;

import mk.ukim.finki.quizbot.Model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Page<Quiz> findByCategoryIgnoreCase(String category, Pageable pageable);

}
