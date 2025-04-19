package mk.ukim.finki.quizbot.Service;

import mk.ukim.finki.quizbot.Model.ApplicationUser;
import mk.ukim.finki.quizbot.Model.DTO.LoginUserDto;
import mk.ukim.finki.quizbot.Model.DTO.RegisterUserDto;
import mk.ukim.finki.quizbot.Model.Enum.UserRoles;
import mk.ukim.finki.quizbot.Repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApplicationUser register(RegisterUserDto input) {
        ApplicationUser user = new ApplicationUser()
                .setFirstName(input.getFirstName())
                .setLastName(input.getLastName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setUserRoles(this.determineUserRole(input.getEmail()));

        return userRepository.save(user);
    }

    public ApplicationUser authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    private static final Pattern STUDENT_EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9]+([._%+-][A-Za-z0-9]+)*@([A-Za-z0-9-]+\\.)*students\\.([A-Za-z0-9-]+\\.)*[A-Za-z]{2,}$"
    );

    private UserRoles determineUserRole(String email) {
        return STUDENT_EMAIL_PATTERN.matcher(email).matches()
                ? UserRoles.STUDENT
                : UserRoles.TEACHER;
    }
}