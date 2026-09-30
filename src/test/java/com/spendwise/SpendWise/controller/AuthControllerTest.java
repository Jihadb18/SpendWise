package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.AuthResponse;
import com.spendwise.SpendWise.dto.LoginRequest;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtService jwtService;

    private AuthController authController;

    @BeforeEach
    void setUp() {

        authenticationManager =
                mock(AuthenticationManager.class);

        userRepository =
                mock(UserRepository.class);

        jwtService =
                mock(JwtService.class);

        authController =
                new AuthController(
                        authenticationManager,
                        userRepository,
                        jwtService
                );
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("jihad@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setName("Jihad");
        user.setEmail("jihad@example.com");
        user.setPassword("encodedPassword");

        Authentication authentication =
                mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(userRepository.findByEmail(
                "jihad@example.com"
        )).thenReturn(
                Optional.of(user)
        );

        when(jwtService.generateToken(
                "jihad@example.com"
        )).thenReturn(
                "fake-jwt-token"
        );

        AuthResponse response =
                authController.login(request);

        assertNotNull(response);

        assertEquals(
                "fake-jwt-token",
                response.getToken()
        );

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(userRepository)
                .findByEmail("jihad@example.com");

        verify(jwtService)
                .generateToken("jihad@example.com");
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("jihad@example.com");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new RuntimeException("Bad credentials")
        );

        assertThrows(
                RuntimeException.class,
                () -> authController.login(request)
        );

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verifyNoInteractions(
                userRepository,
                jwtService
        );
    }

    @Test
    void shouldReturnCurrentUserEmail() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("jihad@example.com");

        String result =
                authController.getCurrentUser(authentication);

        assertEquals(
                "jihad@example.com",
                result
        );

        verify(authentication)
                .getName();
    }
}