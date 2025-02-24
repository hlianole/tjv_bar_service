package cz.cvut.fit.tjv.bar.Service;

import cz.cvut.fit.tjv.bar.DTO.RegisterUserDTO;
import cz.cvut.fit.tjv.bar.Model.Order;
import cz.cvut.fit.tjv.bar.Model.User;
import cz.cvut.fit.tjv.bar.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterUserDTO registerUserDTO) {
        if (userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + registerUserDTO.getEmail() + " already exists");
        }
        User user = new User();
        user.setEmail(registerUserDTO.getEmail());
        user.setName(registerUserDTO.getName());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    public int getUserOrdersCount (User user) {
        return user.getOrders().size();
    }

    public int getUserClosedOrdersCount (User user) {
        return user.getClosedOrders().size();
    }
}
