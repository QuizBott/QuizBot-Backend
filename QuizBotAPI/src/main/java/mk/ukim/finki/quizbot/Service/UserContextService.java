package mk.ukim.finki.quizbot.Service;

import mk.ukim.finki.quizbot.Model.ApplicationUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {
    public ApplicationUser getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof ApplicationUser user) {
            return user;
        }
        throw new RuntimeException("User not authenticated");
    }
}
