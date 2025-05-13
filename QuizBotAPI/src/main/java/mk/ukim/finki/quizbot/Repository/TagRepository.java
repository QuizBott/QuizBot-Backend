package mk.ukim.finki.quizbot.Repository;

import mk.ukim.finki.quizbot.Model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByNameIgnoreCase(String name);
}
