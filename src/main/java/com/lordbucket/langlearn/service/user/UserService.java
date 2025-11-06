package com.lordbucket.langlearn.service.user;

import com.lordbucket.langlearn.dto.req.RegisterRequest;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final boolean skipVerification;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       @Value("${app.auth.skip-verification}") boolean skipVerification) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.skipVerification = skipVerification;
    }

    /**
     * Registers new User, hashes their password and enforces the rest of business logic.
     */
    public User registerUser(RegisterRequest dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalStateException("Email is already taken!");
        }

        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new IllegalStateException("Username is already taken!");
        }

        // Create new user
        User newUser = new User();
        newUser.setUsername(dto.username());
        newUser.setEmail(dto.email());
        newUser.setEnabled(skipVerification);

        // Hash password
        newUser.setPassword(passwordEncoder.encode(dto.password()));

        // Generating verification tokens
        if (skipVerification) {
            newUser.setVerificationToken(null);
            newUser.setVerificationTokenExpiry(null);
        } else {
            String token = UUID.randomUUID().toString();
            newUser.setVerificationToken(token);
            newUser.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        }

        User savedUser = userRepository.save(newUser);

        // Sending email
        if (!skipVerification) {
            emailService.sendVerificationEmail(
                    savedUser.getEmail(),
                    savedUser.getUsername(),
                    savedUser.getVerificationToken()
            );
        }

        return savedUser;
    }

    public String verifyUser(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid verification token!"));

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired. Please request a new one.");
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        return "Email verified successfully!";
    }
}
