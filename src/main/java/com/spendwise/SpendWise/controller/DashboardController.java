package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.dashboard.DashboardResponse;
import com.spendwise.SpendWise.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    @GetMapping("/{userId}")
    public DashboardResponse getDashboard(
            @PathVariable Long userId) {

        return dashboardService.getDashboard(userId);
    }
}