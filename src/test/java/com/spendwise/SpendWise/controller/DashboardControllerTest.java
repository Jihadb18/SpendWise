package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.dashboard.DashboardResponse;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.DashboardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DashboardController dashboardController;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Jihad");
        user.setEmail("jihad@example.com");

        when(authentication.getName())
                .thenReturn("jihad@example.com");

        when(userRepository.findByEmail("jihad@example.com"))
                .thenReturn(Optional.of(user));
    }

    @Test
    void shouldGetDashboardForAuthenticatedUser() {

        DashboardResponse dashboard =
                new DashboardResponse();

        dashboard.setUserId(1L);
        dashboard.setCurrentMonthIncome(
                new BigDecimal("3000")
        );
        dashboard.setCurrentMonthExpenses(
                new BigDecimal("1200")
        );
        dashboard.setBalance(
                new BigDecimal("1800")
        );

        when(dashboardService.getDashboard(1L))
                .thenReturn(dashboard);

        DashboardResponse result =
                dashboardController.getDashboard(
                        authentication
                );

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(
                new BigDecimal("3000"),
                result.getCurrentMonthIncome()
        );
        assertEquals(
                new BigDecimal("1200"),
                result.getCurrentMonthExpenses()
        );
        assertEquals(
                new BigDecimal("1800"),
                result.getBalance()
        );
    }

    @Test
    void shouldUseAuthenticatedUserId() {

        DashboardResponse dashboard =
                new DashboardResponse();

        dashboard.setUserId(1L);

        when(dashboardService.getDashboard(1L))
                .thenReturn(dashboard);

        dashboardController.getDashboard(authentication);

        verify(dashboardService)
                .getDashboard(1L);

        verify(dashboardService, never())
                .getDashboard(2L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail("jihad@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> dashboardController
                        .getDashboard(authentication)
        );

        verify(dashboardService, never())
                .getDashboard(anyLong());
    }

    @Test
    void shouldReturnDashboardResponseFromService() {

        DashboardResponse dashboard =
                new DashboardResponse();

        dashboard.setUserId(1L);
        dashboard.setMonthlySubscriptionCost(
                new BigDecimal("45.50")
        );

        when(dashboardService.getDashboard(1L))
                .thenReturn(dashboard);

        DashboardResponse result =
                dashboardController.getDashboard(
                        authentication
                );

        assertSame(dashboard, result);

        verify(dashboardService)
                .getDashboard(1L);
    }
}