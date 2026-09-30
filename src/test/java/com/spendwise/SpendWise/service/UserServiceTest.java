package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void shouldCreateUser() {

        User user = new User();
        user.setName("Jihad");
        user.setEmail("jihad@example.com");
        user.setPassword("password123");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("Jihad", result.getName());
        assertEquals("jihad@example.com", result.getEmail());

        // Password must be encrypted
        assertNotEquals(
                "password123",
                result.getPassword()
        );

        verify(userRepository).save(user);
    }

    @Test
    void shouldGetAllUsers() {

        User user1 = new User();
        user1.setName("Jihad");

        User user2 = new User();
        user2.setName("Sara");

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Jihad", result.get(0).getName());
        assertEquals("Sara", result.get(1).getName());

        verify(userRepository).findAll();
    }

    @Test
    void shouldGetUserById() {

        User user = new User();
        user.setName("Jihad");
        user.setEmail("jihad@example.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Jihad", result.getName());
        assertEquals("jihad@example.com", result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        User result = userService.getUserById(999L);

        assertNull(result);

        verify(userRepository).findById(999L);
    }

    @Test
    void shouldUpdateUser() {

        User existingUser = new User();
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldPassword");

        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setEmail("new@example.com");
        updatedUser.setPassword("newPassword");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());

        // Password must be encrypted
        assertNotEquals(
                "newPassword",
                result.getPassword()
        );

        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldReturnNullWhenUpdatingNonExistingUser() {

        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setEmail("new@example.com");
        updatedUser.setPassword("newPassword");

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        User result = userService.updateUser(999L, updatedUser);

        assertNull(result);

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {

        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}
