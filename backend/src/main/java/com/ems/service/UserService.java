package com.ems.service;

import com.ems.dto.LoginRequest;
import com.ems.dto.RegisterRequest;
import com.ems.dto.UserDTO;
import com.ems.entity.User;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Service class handling logic for Admin authentication (Registration, Login, and Password validation).
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Registers a new administrator.
     */
    @Transactional
    public UserDTO registerAdmin(RegisterRequest request) {
        // Validate password strength
        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        String hashedPassword = hashPassword(request.getPassword());
        String role = (request.getRole() == null || request.getRole().trim().isEmpty()) ? "ADMIN" : request.getRole().toUpperCase();

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                role
        );

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Authenticates an administrator.
     */
    @Transactional(readOnly = true)
    public UserDTO loginAdmin(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password."));

        String hashedInputPassword = hashPassword(request.getPassword());
        if (!user.getPassword().equals(hashedInputPassword)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        return convertToDTO(user);
    }

    /**
     * Utility method to hash passwords using SHA-256.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: SHA-256 algorithm not found", e);
        }
    }

    /**
     * Converts User entity to UserDTO.
     */
    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Generates a unique authentication token for a session.
     */
    public String generateSessionToken() {
        return UUID.randomUUID().toString();
    }
}
