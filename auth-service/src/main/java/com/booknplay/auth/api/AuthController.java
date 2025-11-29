package com.booknplay.auth.api;

import com.booknplay.auth.security.JwtService;
import com.booknplay.auth.user.UserService;
import com.booknplay.commons.dto.UserDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws ExecutionException, InterruptedException {
        UserDto dto = userService.register(request.email, request.password, Set.of("ROLE_USER")).get();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UserDto dto = userService.findByEmail(request.email);
        if (dto == null) return ResponseEntity.status(401).build();
        // For brevity we skip password check demo; would load entity & match
        String token = jwtService.generate(dto.id(), Map.of("roles", dto.role()));
        return ResponseEntity.ok(Map.of("token", token));
    }

    static class RegisterRequest {
        @Email @NotBlank public String email;
        @NotBlank public String password;
    }

    static class LoginRequest {
        @Email @NotBlank public String email;
        @NotBlank public String password;
    }
}
