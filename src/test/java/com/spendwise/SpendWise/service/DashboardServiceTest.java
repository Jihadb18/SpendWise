package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.dto.dashboard.BudgetDashboardResponse;
import com.spendwise.SpendWise.dto.dashboard.DashboardResponse;
import com.spendwise.SpendWise.dto.subscription.SubscriptionResponse;
import com.spendwise.SpendWise.entity.Budget;
import com.spendwise.SpendWise.entity.Subscription;
import com.spendwise.SpendWise.entity.SubscriptionFrequency;
import com.spendwise.SpendWise.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    private ExpenseRepository expenseRepository;
    private IncomeService incomeService;
    private SubscriptionService subscriptionService;
    private BudgetService budgetService;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {

        expenseRepository = mock(ExpenseRepository.class);
        incomeService = mock(IncomeService.class);
        subscriptionService = mock(SubscriptionService.class);
        budgetService = mock(BudgetService.class);

        dashboardService = new DashboardService(
                expenseRepository,
                incomeService,
                subscriptionService,
                budgetService
        );
    }

    @Test
    void shouldBuildDashboardSuccessfully() {

        Long userId = 2L;

        LocalDate today = LocalDate.now();
        LocalDate currentMonthStart =
                today.withDayOfMonth(1);
        LocalDate currentMonthEnd =
                today.withDayOfMonth(
                        today.lengthOfMonth()
                );

        LocalDate previousMonthDate =
                today.minusMonths(1);

        LocalDate previousMonthStart =
                previousMonthDate.withDayOfMonth(1);
        LocalDate previousMonthEnd =
                previousMonthDate.withDayOfMonth(
                        previousMonthDate.lengthOfMonth()
                );

        // Income
        when(incomeService.getIncomeBetweenDates(
                userId,
                currentMonthStart,
                currentMonthEnd
        )).thenReturn(
                new BigDecimal("3000.00")
        );

        // Current expenses
        when(expenseRepository.getExpensesBetweenDates(
                userId,
                currentMonthStart,
                currentMonthEnd
        )).thenReturn(
                new BigDecimal("1200.00")
        );

        // Previous expenses
        when(expenseRepository.getExpensesBetweenDates(
                userId,
                previousMonthStart,
                previousMonthEnd
        )).thenReturn(
                new BigDecimal("1000.00")
        );

        // Subscriptions
        when(subscriptionService.getMonthlyCost(userId))
                .thenReturn(
                        new BigDecimal("45.00")
                );

        when(subscriptionService.getUpcomingSubscriptions(userId))
                .thenReturn(List.of());

        // Budgets
        when(
                budgetService.getBudgetsByUserAndMonth(
                        userId,
                        currentMonthStart
                )
        ).thenReturn(List.of());

        // Spending by category
        when(
                expenseRepository
                        .getExpensesByCategoryAndDateRange(
                                userId,
                                currentMonthStart,
                                currentMonthEnd
                        )
        ).thenReturn(
                List.of(
                        new Object[]{
                                "Food",
                                new BigDecimal("500.00")
                        },
                        new Object[]{
                                "Transport",
                                new BigDecimal("200.00")
                        }
                )
        );

        DashboardResponse result =
                dashboardService.getDashboard(userId);

        assertNotNull(result);

        assertEquals(
                userId,
                result.getUserId()
        );

        assertEquals(
                new BigDecimal("3000.00"),
                result.getCurrentMonthIncome()
        );

        assertEquals(
                new BigDecimal("1200.00"),
                result.getCurrentMonthExpenses()
        );

        assertEquals(
                new BigDecimal("1800.00"),
                result.getBalance()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                result.getPreviousMonthExpenses()
        );

        // (1200 - 1000) / 1000 * 100 = 20%
        assertEquals(
                new BigDecimal("20.00"),
                result.getExpenseChangePercentage()
        );

        assertEquals(
                new BigDecimal("45.00"),
                result.getMonthlySubscriptionCost()
        );

        Map<String, BigDecimal> spending =
                result.getSpendingByCategory();

        assertEquals(
                new BigDecimal("500.00"),
                spending.get("Food")
        );

        assertEquals(
                new BigDecimal("200.00"),
                spending.get("Transport")
        );

        assertTrue(result.getBudgets().isEmpty());
        assertTrue(result.getUpcomingSubscriptions().isEmpty());

        verify(incomeService)
                .getIncomeBetweenDates(
                        userId,
                        currentMonthStart,
                        currentMonthEnd
                );

        verify(expenseRepository)
                .getExpensesBetweenDates(
                        userId,
                        currentMonthStart,
                        currentMonthEnd
                );

        verify(expenseRepository)
                .getExpensesBetweenDates(
                        userId,
                        previousMonthStart,
                        previousMonthEnd
                );
    }

    @Test
    void shouldReplaceNullValuesWithZero() {

        Long userId = 2L;

        LocalDate today = LocalDate.now();

        LocalDate currentMonthStart =
                today.withDayOfMonth(1);

        LocalDate currentMonthEnd =
                today.withDayOfMonth(
                        today.lengthOfMonth()
                );

        LocalDate previousMonthDate =
                today.minusMonths(1);

        LocalDate previousMonthStart =
                previousMonthDate.withDayOfMonth(1);

        LocalDate previousMonthEnd =
                previousMonthDate.withDayOfMonth(
                        previousMonthDate.lengthOfMonth()
                );

        when(incomeService.getIncomeBetweenDates(
                userId,
                currentMonthStart,
                currentMonthEnd
        )).thenReturn(null);

        when(expenseRepository.getExpensesBetweenDates(
                userId,
                currentMonthStart,
                currentMonthEnd
        )).thenReturn(null);

        when(expenseRepository.getExpensesBetweenDates(
                userId,
                previousMonthStart,
                previousMonthEnd
        )).thenReturn(null);

        when(subscriptionService.getMonthlyCost(userId))
                .thenReturn(BigDecimal.ZERO);

        when(subscriptionService.getUpcomingSubscriptions(userId))
                .thenReturn(List.of());

        when(
                budgetService.getBudgetsByUserAndMonth(
                        userId,
                        currentMonthStart
                )
        ).thenReturn(List.of());

        when(
                expenseRepository
                        .getExpensesByCategoryAndDateRange(
                                userId,
                                currentMonthStart,
                                currentMonthEnd
                        )
        ).thenReturn(List.of());

        DashboardResponse result =
                dashboardService.getDashboard(userId);

        assertNotNull(result);

        assertEquals(
                BigDecimal.ZERO,
                result.getCurrentMonthIncome()
        );

        assertEquals(
                BigDecimal.ZERO,
                result.getCurrentMonthExpenses()
        );

        assertEquals(
                BigDecimal.ZERO,
                result.getBalance()
        );

        assertEquals(
                BigDecimal.ZERO,
                result.getPreviousMonthExpenses()
        );

        assertEquals(
                BigDecimal.ZERO,
                result.getExpenseChangePercentage()
        );
    }

    @Test
    void shouldCalculateBudgetStatusesCorrectly() {

        Long userId = 2L;

        LocalDate today = LocalDate.now();
        LocalDate currentMonthStart =
                today.withDayOfMonth(1);

        LocalDate currentMonthEnd =
                today.withDayOfMonth(
                        today.lengthOfMonth()
                );

        // Income
        when(incomeService.getIncomeBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(BigDecimal.ZERO);

        // Current expenses
        when(expenseRepository.getExpensesBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(BigDecimal.ZERO);

        // Previous expenses
        when(expenseRepository.getExpensesBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(BigDecimal.ZERO);

        // Subscriptions
        when(subscriptionService.getMonthlyCost(userId))
                .thenReturn(BigDecimal.ZERO);

        when(subscriptionService.getUpcomingSubscriptions(userId))
                .thenReturn(List.of());

        // =========================
        // Budgets
        // =========================

        Budget foodBudget = new Budget();
        foodBudget.setCategory("Food");
        foodBudget.setAmount(
                new BigDecimal("1000.00")
        );
        foodBudget.setMonth(currentMonthStart);

        Budget transportBudget = new Budget();
        transportBudget.setCategory("Transport");
        transportBudget.setAmount(
                new BigDecimal("1000.00")
        );
        transportBudget.setMonth(currentMonthStart);

        Budget rentBudget = new Budget();
        rentBudget.setCategory("Rent");
        rentBudget.setAmount(
                new BigDecimal("1000.00")
        );
        rentBudget.setMonth(currentMonthStart);

        when(
                budgetService.getBudgetsByUserAndMonth(
                        userId,
                        currentMonthStart
                )
        ).thenReturn(
                List.of(
                        foodBudget,
                        transportBudget,
                        rentBudget
                )
        );

        // =========================
        // Spent
        // =========================

        // Food = 500 / 1000 = 50%
        when(budgetService.getSpent(
                userId,
                "Food",
                currentMonthStart
        )).thenReturn(
                new BigDecimal("500.00")
        );

        // Transport = 950 / 1000 = 95%
        when(budgetService.getSpent(
                userId,
                "Transport",
                currentMonthStart
        )).thenReturn(
                new BigDecimal("950.00")
        );

        // Rent = 1000 / 1000 = 100%
        when(budgetService.getSpent(
                userId,
                "Rent",
                currentMonthStart
        )).thenReturn(
                new BigDecimal("1000.00")
        );

        // =========================
        // Remaining
        // =========================

        when(budgetService.getRemaining(
                new BigDecimal("1000.00"),
                new BigDecimal("500.00")
        )).thenReturn(
                new BigDecimal("500.00")
        );

        when(budgetService.getRemaining(
                new BigDecimal("1000.00"),
                new BigDecimal("950.00")
        )).thenReturn(
                new BigDecimal("50.00")
        );

        when(budgetService.getRemaining(
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00")
        )).thenReturn(
                BigDecimal.ZERO
        );

        // =========================
        // Percentage used
        // =========================

        when(budgetService.getPercentageUsed(
                new BigDecimal("1000.00"),
                new BigDecimal("500.00")
        )).thenReturn(
                new BigDecimal("50.00")
        );

        when(budgetService.getPercentageUsed(
                new BigDecimal("1000.00"),
                new BigDecimal("950.00")
        )).thenReturn(
                new BigDecimal("95.00")
        );

        when(budgetService.getPercentageUsed(
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00")
        )).thenReturn(
                new BigDecimal("100.00")
        );

        // =========================
        // Spending by category
        // =========================

        when(
                expenseRepository
                        .getExpensesByCategoryAndDateRange(
                                userId,
                                currentMonthStart,
                                currentMonthEnd
                        )
        ).thenReturn(List.of());

        // =========================
        // Execute
        // =========================

        DashboardResponse result =
                dashboardService.getDashboard(userId);

        // =========================
        // Assertions
        // =========================

        assertNotNull(result);

        assertEquals(
                3,
                result.getBudgets().size()
        );

        BudgetDashboardResponse food =
                result.getBudgets().get(0);

        BudgetDashboardResponse transport =
                result.getBudgets().get(1);

        BudgetDashboardResponse rent =
                result.getBudgets().get(2);

        // Food
        assertEquals(
                "Food",
                food.getCategory()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                food.getBudget()
        );

        assertEquals(
                new BigDecimal("500.00"),
                food.getSpent()
        );

        assertEquals(
                new BigDecimal("500.00"),
                food.getRemaining()
        );

        assertEquals(
                new BigDecimal("50.00"),
                food.getPercentageUsed()
        );

        assertEquals(
                "OK",
                food.getStatus()
        );

        // Transport
        assertEquals(
                "Transport",
                transport.getCategory()
        );

        assertEquals(
                new BigDecimal("950.00"),
                transport.getSpent()
        );

        assertEquals(
                new BigDecimal("50.00"),
                transport.getRemaining()
        );

        assertEquals(
                new BigDecimal("95.00"),
                transport.getPercentageUsed()
        );

        assertEquals(
                "WARNING",
                transport.getStatus()
        );

        // Rent
        assertEquals(
                "Rent",
                rent.getCategory()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                rent.getSpent()
        );

        assertEquals(
                BigDecimal.ZERO,
                rent.getRemaining()
        );

        assertEquals(
                new BigDecimal("100.00"),
                rent.getPercentageUsed()
        );

        assertEquals(
                "EXCEEDED",
                rent.getStatus()
        );
    }
    @Test
    void shouldMapUpcomingSubscriptions() {

        Long userId = 2L;

        LocalDate today = LocalDate.now();

        Subscription subscription =
                new Subscription();

        subscription.setId(10L);
        subscription.setName("Netflix");
        subscription.setAmount(
                new BigDecimal("15.99")
        );
        subscription.setNextPaymentDate(
                today.plusDays(3)
        );
        subscription.setFrequency(
                SubscriptionFrequency.MONTHLY
        );

        when(incomeService.getIncomeBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(BigDecimal.ZERO);

        when(expenseRepository.getExpensesBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(BigDecimal.ZERO);

        when(subscriptionService.getMonthlyCost(userId))
                .thenReturn(
                        new BigDecimal("15.99")
                );

        when(subscriptionService.getUpcomingSubscriptions(userId))
                .thenReturn(List.of(subscription));

        when(
                budgetService.getBudgetsByUserAndMonth(
                        eq(userId),
                        any(LocalDate.class)
                )
        ).thenReturn(List.of());

        when(
                expenseRepository
                        .getExpensesByCategoryAndDateRange(
                                eq(userId),
                                any(LocalDate.class),
                                any(LocalDate.class)
                        )
        ).thenReturn(List.of());

        DashboardResponse result =
                dashboardService.getDashboard(userId);

        assertNotNull(result);
        assertEquals(
                1,
                result.getUpcomingSubscriptions().size()
        );

        SubscriptionResponse response =
                result.getUpcomingSubscriptions().get(0);

        assertEquals(
                10L,
                response.getId()
        );

        assertEquals(
                "Netflix",
                response.getName()
        );

        assertEquals(
                new BigDecimal("15.99"),
                response.getAmount()
        );

        assertEquals(
                today.plusDays(3),
                response.getNextPaymentDate()
        );

        assertEquals(
                SubscriptionFrequency.MONTHLY,
                response.getFrequency()
        );
    }

    @Test
    void shouldHandleSpendingByCategory() {

        Long userId = 2L;

        LocalDate today = LocalDate.now();
        LocalDate currentMonthStart =
                today.withDayOfMonth(1);
        LocalDate currentMonthEnd =
                today.withDayOfMonth(
                        today.lengthOfMonth()
                );

        when(incomeService.getIncomeBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(BigDecimal.ZERO);

        when(expenseRepository.getExpensesBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(BigDecimal.ZERO);

        when(subscriptionService.getMonthlyCost(userId))
                .thenReturn(BigDecimal.ZERO);

        when(subscriptionService.getUpcomingSubscriptions(userId))
                .thenReturn(List.of());

        when(
                budgetService.getBudgetsByUserAndMonth(
                        userId,
                        currentMonthStart
                )
        ).thenReturn(List.of());

        when(
                expenseRepository
                        .getExpensesByCategoryAndDateRange(
                                userId,
                                currentMonthStart,
                                currentMonthEnd
                        )
        ).thenReturn(
                List.of(
                        new Object[]{
                                "Food",
                                new BigDecimal("800.50")
                        },
                        new Object[]{
                                "Education",
                                new BigDecimal("300.00")
                        },
                        new Object[]{
                                "Transport",
                                new BigDecimal("150.25")
                        }
                )
        );

        DashboardResponse result =
                dashboardService.getDashboard(userId);

        Map<String, BigDecimal> spending =
                result.getSpendingByCategory();

        assertNotNull(spending);
        assertEquals(3, spending.size());

        assertEquals(
                new BigDecimal("800.50"),
                spending.get("Food")
        );

        assertEquals(
                new BigDecimal("300.00"),
                spending.get("Education")
        );

        assertEquals(
                new BigDecimal("150.25"),
                spending.get("Transport")
        );
    }
}