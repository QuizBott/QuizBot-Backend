package mk.ukim.finki.quizbot.Mapper;

import mk.ukim.finki.quizbot.Model.ApplicationUser;
import mk.ukim.finki.quizbot.Model.DTO.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toUserDTO(ApplicationUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getFirstName() + " " + user.getLastName());
        return userDTO;
    }
}
