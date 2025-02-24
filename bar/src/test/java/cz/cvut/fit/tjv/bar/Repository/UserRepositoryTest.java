package cz.cvut.fit.tjv.bar.Repository;

import cz.cvut.fit.tjv.bar.Model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenSaveUserThenReturnSavedUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("password");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("Test User", saved.getName());
        assertEquals("test@test.com", saved.getEmail());
    }

    @Test
    void whenFindByEmailThenReturnUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@test.com");

        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

}
