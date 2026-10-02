package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.UpdateProfileRequest;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public User getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(
                authentication.getName()
        );
    }

    @PutMapping("/me")
    public User updateCurrentUser(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        return userService.updateCurrentUser(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/{id:\\d+}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id:\\d+}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id:\\d+}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}