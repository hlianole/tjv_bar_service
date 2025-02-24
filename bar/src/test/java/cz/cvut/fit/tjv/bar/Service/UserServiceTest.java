package cz.cvut.fit.tjv.bar.Service;

import cz.cvut.fit.tjv.bar.DTO.RegisterUserDTO;
import cz.cvut.fit.tjv.bar.Model.Order;
import cz.cvut.fit.tjv.bar.Model.User;
import cz.cvut.fit.tjv.bar.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void whenRegisterUserThenUserIsSaved() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("test@example.com");
        dto.setName("Test User");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        userService.registerUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(dto.getEmail(), savedUser.getEmail());
        assertEquals(dto.getName(), savedUser.getName());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("ROLE_USER", savedUser.getRole());
    }

    @Test
    void whenGetUserOrdersCountThenReturnsCorrectCount() {
        User user = new User();
        user.setOrders(List.of(new Order(), new Order(), new Order()));

        int orderCount = userService.getUserOrdersCount(user);

        assertEquals(3, orderCount);
    }
}
