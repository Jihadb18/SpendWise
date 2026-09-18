package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.dashboard.DashboardResponse;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.DashboardService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    public DashboardController(
            DashboardService dashboardService,
            UserRepository userRepository) {

        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public DashboardResponse getDashboard(
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return dashboardService.getDashboard(
                currentUser.getId()
        );
    }
}
