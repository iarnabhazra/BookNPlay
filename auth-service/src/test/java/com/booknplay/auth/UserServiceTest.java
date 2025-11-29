package com.booknplay.auth;

import com.booknplay.auth.user.UserRepository;
import com.booknplay.auth.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@DataJpaTest
@Import(UserServiceTest.Config.class)
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    void registerCreatesUser() throws Exception {
        var dto = userService.register("test@example.com", "secret", Set.of("ROLE_USER")).get();
    Assertions.assertNotNull(dto.id());
        Assertions.assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class Config {
        @org.springframework.context.annotation.Bean
        PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
        @org.springframework.context.annotation.Bean
        UserService userService(UserRepository repo, PasswordEncoder encoder){return new UserService(repo, encoder);}    }
}
