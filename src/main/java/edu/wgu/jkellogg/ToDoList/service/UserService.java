package edu.wgu.jkellogg.ToDoList.service;

import edu.wgu.jkellogg.ToDoList.domain.User;
import edu.wgu.jkellogg.ToDoList.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrCreateUser(OAuth2User oauthUser) {
        String googleId = oauthUser.getAttribute("sub");

        return userRepository.findByGoogleId(googleId)
                .orElseGet(() -> createNewUser(oauthUser));
    }

    private User createNewUser(OAuth2User oauthUser) {
        User user = new User();
        user.setGoogleId(oauthUser.getAttribute("sub"));
        user.setEmail(oauthUser.getAttribute("email"));
        user.setName(oauthUser.getAttribute("name"));

        System.out.println("Google ID: " + user.getGoogleId());

        return userRepository.save(user);
    }
}

