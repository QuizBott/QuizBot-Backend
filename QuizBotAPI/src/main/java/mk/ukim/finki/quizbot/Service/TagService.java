package mk.ukim.finki.quizbot.Service;

import mk.ukim.finki.quizbot.Model.DTO.TagDTO;
import mk.ukim.finki.quizbot.Model.Quiz;
import mk.ukim.finki.quizbot.Model.Tag;
import mk.ukim.finki.quizbot.Repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getOrCreateTags(List<TagDTO> tags, Quiz quiz) {
        List<Tag> tagList = new ArrayList<>();
        for (TagDTO tag : tags) {
            Tag existingTag = tagRepository.findByNameIgnoreCase(tag.name());

            if (existingTag != null) {
                existingTag.addQuiz(quiz);
                tagList.add(existingTag);
            } else {
                Tag newTag = new Tag(tag.name());
                newTag.addQuiz(quiz);
                tagRepository.save(newTag);
                tagList.add(newTag);
            }
        }
        return tagList;
    }
}
