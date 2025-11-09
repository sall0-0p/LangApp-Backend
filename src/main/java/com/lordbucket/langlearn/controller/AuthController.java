package com.lordbucket.langlearn.controller;

import com.lordbucket.langlearn.dto.req.LoginRequest;
import com.lordbucket.langlearn.dto.req.RegisterRequest;
import com.lordbucket.langlearn.dto.res.AuthResponse;
import com.lordbucket.langlearn.misc.auth.JwtTokenProvider;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.service.mapper.UserMapper;
import com.lordbucket.langlearn.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    /**
     * Endpoint for user registration.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            userService.registerUser(registerRequest);
            return ResponseEntity.ok("User registered successfully. Please verify your email.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint for logging in.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.usernameOrEmail(),
                        loginRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    /**
     * Endpoint for getting current user.
     * */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    /**
     * Endpoint for logging out.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("User logged out successfully.");
    }
}
