package com.sinandemir.todoapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.sinandemir.todoapp.entities.Role;
import com.sinandemir.todoapp.entities.User;
import com.sinandemir.todoapp.repositories.UserRepository;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepos;

    @InjectMocks
    private UserService cut;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Test existsByEmail")
    void should_return_true_exists_by_user_email() {
        User user = new User();
        String fakeEmail = "xxx@fakeEmail.com";
        user.setEmail(fakeEmail);

        when(userRepos.existsByEmail(fakeEmail)).thenReturn(true);

        Boolean result = cut.existsByEmail(fakeEmail);

        assertNotNull(result);
        assertTrue(result);

        verify(userRepos).existsByEmail(fakeEmail);
    }

    @Test
    @DisplayName("Test existsByEmail return false")
    void should_return_false_exists_by_user_email() {
        User user = new User();
        String fakeEmail = "xxx@fakeEmail.com";
        user.setEmail(fakeEmail);

        when(userRepos.existsByEmail(fakeEmail)).thenReturn(false);

        Boolean result = cut.existsByEmail(fakeEmail);

        assertNotNull(result);
        assertFalse(result);

        verify(userRepos).existsByEmail(fakeEmail);
    }

    @Test
    @DisplayName("Test existsByUsername")
    void should_return_true_exists_by_username() {
        User user = new User();
        String fakeUsername = "fakeUsername";
        user.setUsername(fakeUsername);

        when(userRepos.existsByUsername(fakeUsername)).thenReturn(true);

        Boolean result = cut.existsByUsername(fakeUsername);

        assertNotNull(result);
        assertTrue(result);

        verify(userRepos).existsByUsername(fakeUsername);
    }

    @Test
    @DisplayName("Test existsByUsername return false")
    void should_return_false_exists_by_username() {
        User user = new User();
        String fakeUsername = "fakeUsername";
        user.setUsername(fakeUsername);

        when(userRepos.existsByUsername(fakeUsername)).thenReturn(false);

        Boolean result = cut.existsByUsername(fakeUsername);

        assertNotNull(result);
        assertFalse(result);

        verify(userRepos).existsByUsername(fakeUsername);
    }

    @Test
    @DisplayName("Test save")
    void should_save_user_and_return_user() {
        Set<Role> roles = new HashSet<Role>();
        roles.add(new Role(1L, "FAKE_ROLE"));

        User user = new User();
        user.setId(1L);
        user.setName("fakeValue");
        user.setEmail("fakeValue");
        user.setPassword("fakeValue");
        user.setUsername("fakeValue");
        user.setRoles(roles);

        when(userRepos.save(user)).thenReturn(user);

        cut.save(user);

        verify(userRepos).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertNotNull(capturedUser);
        assertNotNull(capturedUser.getId());
        assertNotNull(capturedUser.getName());
        assertNotNull(capturedUser.getEmail());
        assertNotNull(capturedUser.getPassword());
        assertNotNull(capturedUser.getUsername());
        assertNotNull(capturedUser.getRoles());

        assertEquals(capturedUser.getId(), user.getId());
        assertEquals(capturedUser.getName(), user.getName());
        assertEquals(capturedUser.getEmail(), user.getEmail());
        assertEquals(capturedUser.getPassword(), user.getPassword());
        assertEquals(capturedUser.getUsername(), user.getUsername());
        assertEquals(capturedUser.getRoles().size(), user.getRoles().size());

    }

    @Test
    @DisplayName("Test findByUsernameOrEmail")
    void should_find_by_username_or_email_and_return_user() {
        String username = "fakeUsername";
        String email = "xxx@fakeEmail.com";

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);

        when(userRepos.findByUsernameOrEmail(username, email)).thenReturn(Optional.of(user));

        User result = cut.findByUsernameOrEmail(username, email).get();

        assertNotNull(result);

        assertEquals(username, result.getUsername());
        assertEquals(email, user.getEmail());

        verify(userRepos).findByUsernameOrEmail(username, email);
    }

    @Test
    @DisplayName("Test findByUsernameOrEmail return null")
    void should_return_null_find_by_username_or_email() {
        String username = "fakeUsername";
        String email = "xxx@fakeEmail.com";

        when(userRepos.findByUsernameOrEmail(username, email)).thenReturn(Optional.empty());

        Optional<User> result = cut.findByUsernameOrEmail(username, email);

        assertFalse(result.isPresent());

        verify(userRepos).findByUsernameOrEmail(username, email);

    }

    @Test
    @DisplayName("Test findById")
    void should_find_user_by_user_id_and_return_the_user() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepos.findById(userId)).thenReturn(Optional.of(user));

        User result = cut.findById(userId).get();

        assertNotNull(result);
        assertNotNull(result.getId());

        assertEquals(userId, result.getId());

        verify(userRepos).findById(userId);

    }

    @Test
    @DisplayName("Test findbyId return null")
    void should_return_null_find_user_by_user_id(){
        Long userId = 1L;

        when(userRepos.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = cut.findById(userId);

        assertFalse(result.isPresent());

        verify(userRepos).findById(userId);
    }
}
