package com.booknplay.auth.user;

import com.booknplay.commons.dto.UserDto;
// Removed Lombok usage; plain service class.
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    private final ExecutorService registrationExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Transactional
    public CompletableFuture<UserDto> register(String email, String rawPassword, Set<String> roles) {
        return CompletableFuture.supplyAsync(() -> {
            if (repository.existsByEmail(email)) {
                throw new IllegalStateException("Email already exists");
            }
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRoles(roles);
            user.setEnabled(true);
            user.setCreatedAt(Instant.now());
            repository.save(user);
            return new UserDto(user.getId(), user.getEmail(), String.join(",", user.getRoles()));
        }, registrationExecutor);
    }

    @Cacheable(cacheNames = "users", key = "#email")
    public UserDto findByEmail(String email) {
    return repository.findByEmail(email)
        .map(u -> new UserDto(u.getId(), u.getEmail(), String.join(",", u.getRoles())))
        .orElse(null);
    }
}
