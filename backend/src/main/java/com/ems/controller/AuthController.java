package com.ems.controller;

import com.ems.dto.*;
import com.ems.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling REST endpoints for Admin Authentication.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow frontend to communicate from other ports if running separately
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint to register a new administrator.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserDTO registeredUser = userService.registerAdmin(request);
            return new ResponseEntity<>(
                    new ApiResponse<>(true, "Registration successful!", registeredUser),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Endpoint for administrator login.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            UserDTO user = userService.loginAdmin(request);
            String token = userService.generateSessionToken();
            AuthResponse response = new AuthResponse(token, user);
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful!", response));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, e.getMessage()),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    /**
     * Endpoint to simulate logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // Since JWT/tokens are handled client-side in standard REST, client deletes token.
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout successful!"));
    }
}
