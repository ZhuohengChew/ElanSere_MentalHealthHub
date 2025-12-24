package com.mentalhealthhub.service;

import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Register a new user with hashed password
     */
    public User registerUser(String email, String password, String name, UserRole role) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Validate password
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(password);

        // Create new user
        User user = User.builder()
                .email(email)
                .password(hashedPassword)
                .name(name)
                .role(role)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * Authenticate user with email and password
     */
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        // Check if user is active
        if (!user.getActive()) {
            return Optional.empty();
        }

        // Check password (supports both hashed and plain text for migration)
        String storedPassword = user.getPassword();
        boolean passwordMatches = false;

        // Check if password is already hashed (starts with $2a$ or $2b$)
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
            passwordMatches = passwordEncoder.matches(password, storedPassword);
        } else {
            // Legacy plain text password support (for existing data)
            passwordMatches = storedPassword.equals(password);

            // If plain text matches, upgrade to hashed password
            if (passwordMatches) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        }

        return passwordMatches ? Optional.of(user) : Optional.empty();
    }

    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Update user password
     */
    public void updatePassword(User user, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
