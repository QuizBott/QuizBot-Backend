package mk.ukim.finki.quizbot.Controller;


import mk.ukim.finki.quizbot.Mapper.UserMapper;
import mk.ukim.finki.quizbot.Model.DTO.UserDTO;
import mk.ukim.finki.quizbot.Service.UserContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserContextService userContextService;
    private final UserMapper userMapper;

    public UserController(UserContextService userContextService, UserMapper userMapper) {
        this.userContextService = userContextService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<UserDTO> getUser() {
        return ResponseEntity.ok(userMapper.toUserDTO(userContextService.getCurrentUser()));
    }
}
