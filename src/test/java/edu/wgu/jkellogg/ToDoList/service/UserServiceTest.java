package edu.wgu.jkellogg.ToDoList.service;

import edu.wgu.jkellogg.ToDoList.domain.User;
import edu.wgu.jkellogg.ToDoList.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private OAuth2User oauthUser;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
//        user.setId(1L);
        user.setGoogleId("google-123");
        user.setEmail("test@example.com");
        user.setName("Test User");
    }

    @Test
    void findOrCreateUser_WhenUserExists_ShouldReturnUser() {
        when(oauthUser.getAttribute("sub")).thenReturn("google-123");
        when(userRepository.findByGoogleId("google-123")).thenReturn(Optional.of(user));

        User result = userService.findOrCreateUser(oauthUser);

        assertNotNull(result);
        assertEquals("google-123", result.getGoogleId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findOrCreateUser_WhenUserDoesNotExist_ShouldCreateAndReturnUser() {
        when(oauthUser.getAttribute("sub")).thenReturn("new-google-id");
        when(oauthUser.getAttribute("email")).thenReturn("new@example.com");
        when(oauthUser.getAttribute("name")).thenReturn("New User");
        when(userRepository.findByGoogleId("new-google-id")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.findOrCreateUser(oauthUser);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("New User", result.getName());
        verify(userRepository).save(any(User.class));
    }
}
