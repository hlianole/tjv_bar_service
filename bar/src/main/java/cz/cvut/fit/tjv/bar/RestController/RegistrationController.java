package cz.cvut.fit.tjv.bar.RestController;

import cz.cvut.fit.tjv.bar.DTO.RegisterUserDTO;
import cz.cvut.fit.tjv.bar.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Registration controller", description = "CRUD for registering users")
public class RegistrationController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Email already exists"),
            @ApiResponse(responseCode = "500", description = "Registration failed due to a server error")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        try {
            userService.registerUser(registerUserDTO);
            System.out.println("User with email " + registerUserDTO.getEmail() + " registered successfully");
            return ResponseEntity.ok().body(Map.of("massage", "User registered successfully"));
        }
        catch (IllegalArgumentException e) {
            System.out.println("User with email " + registerUserDTO.getEmail() + " already exists");
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }
        catch (Exception e) {
            System.out.println("Error occurred while registering user with email " + registerUserDTO.getEmail());
            return ResponseEntity.internalServerError().body(Map.of("error", "Registration failed"));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("type", "ValidationError");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

}
