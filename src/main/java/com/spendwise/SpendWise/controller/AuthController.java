package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.AuthResponse;
import com.spendwise.SpendWise.dto.LoginRequest;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        System.out.println("LOGIN EMAIL = " + request.getEmail());

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            System.out.println("AUTHENTICATION SUCCESS");

        } catch (Exception e) {

            System.out.println("AUTHENTICATION FAILED: "
                    + e.getClass().getName());

            System.out.println("MESSAGE: "
                    + e.getMessage());

            throw e;
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}