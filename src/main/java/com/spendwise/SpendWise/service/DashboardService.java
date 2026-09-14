package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.dto.dashboard.BudgetDashboardResponse;
import com.spendwise.SpendWise.dto.dashboard.DashboardResponse;
import com.spendwise.SpendWise.dto.subscription.SubscriptionResponse;
import com.spendwise.SpendWise.entity.Budget;
import com.spendwise.SpendWise.entity.Subscription;
import com.spendwise.SpendWise.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final IncomeService incomeService;
    private final SubscriptionService subscriptionService;
    private final BudgetService budgetService;

    public DashboardService(
            ExpenseRepository expenseRepository,
            IncomeService incomeService,
            SubscriptionService subscriptionService,
            BudgetService budgetService) {

        this.expenseRepository = expenseRepository;
        this.incomeService = incomeService;
        this.subscriptionService = subscriptionService;
        this.budgetService = budgetService;
    }

    public DashboardResponse getDashboard(Long userId) {

        LocalDate today = LocalDate.now();

        // =========================
        // Current month
        // =========================

        LocalDate currentMonthStart =
                today.withDayOfMonth(1);

        LocalDate currentMonthEnd =
                today.withDayOfMonth(
                        today.lengthOfMonth()
                );

        // =========================
        // Previous month
        // =========================

        LocalDate previousMonthDate =
                today.minusMonths(1);

        LocalDate previousMonthStart =
                previousMonthDate.withDayOfMonth(1);

        LocalDate previousMonthEnd =
                previousMonthDate.withDayOfMonth(
                        previousMonthDate.lengthOfMonth()
                );

        // =========================
        // Income
        // =========================

        BigDecimal currentMonthIncome =
                incomeService.getIncomeBetweenDates(
                        userId,
                        currentMonthStart,
                        currentMonthEnd
                );

        // =========================
        // Current expenses
        // =========================

        BigDecimal currentMonthExpenses =
                expenseRepository.getExpensesBetweenDates(
                        userId,
                        currentMonthStart,
                        currentMonthEnd
                );

        // =========================
        // Previous expenses
        // =========================

        BigDecimal previousMonthExpenses =
                expenseRepository.getExpensesBetweenDates(
                        userId,
                        previousMonthStart,
                        previousMonthEnd
                );

        // =========================
        // Prevent null values
        // =========================

        if (currentMonthIncome == null) {
            currentMonthIncome = BigDecimal.ZERO;
        }

        if (currentMonthExpenses == null) {
            currentMonthExpenses = BigDecimal.ZERO;
        }

        if (previousMonthExpenses == null) {
            previousMonthExpenses = BigDecimal.ZERO;
        }

        // =========================
        // Balance
        // =========================

        BigDecimal balance =
                currentMonthIncome.subtract(
                        currentMonthExpenses
                );

        // =========================
        // Expense change percentage
        // =========================

        BigDecimal expenseChangePercentage =
                calculateExpenseChange(
                        previousMonthExpenses,
                        currentMonthExpenses
                );

        // =========================
        // Monthly subscriptions
        // =========================

        BigDecimal monthlySubscriptionCost =
                subscriptionService.getMonthlyCost(userId);

        // =========================
        // Upcoming subscriptions
        // =========================

        List<Subscription> subscriptions =
                subscriptionService.getUpcomingSubscriptions(userId);

        List<SubscriptionResponse> upcomingSubscriptions =
                new ArrayList<>();

        for (Subscription subscription : subscriptions) {

            SubscriptionResponse subscriptionResponse =
                    new SubscriptionResponse(
                            subscription.getId(),
                            subscription.getName(),
                            subscription.getAmount(),
                            subscription.getNextPaymentDate(),
                            subscription.getFrequency()
                    );

            upcomingSubscriptions.add(
                    subscriptionResponse
            );
        }

        // =========================
        // Current month budgets
        // =========================

        List<Budget> currentMonthBudgets =
                budgetService.getBudgetsByUserAndMonth(
                        userId,
                        currentMonthStart
                );

        List<BudgetDashboardResponse> budgetResponses =
                new ArrayList<>();

        for (Budget budget : currentMonthBudgets) {

            BigDecimal spent =
                    budgetService.getSpent(
                            userId,
                            budget.getCategory(),
                            budget.getMonth()
                    );

            BigDecimal remaining =
                    budgetService.getRemaining(
                            budget.getAmount(),
                            spent
                    );

            BigDecimal percentageUsed =
                    budgetService.getPercentageUsed(
                            budget.getAmount(),
                            spent
                    );

            String status;

            if (percentageUsed.compareTo(
                    BigDecimal.valueOf(100)) >= 0) {

                status = "EXCEEDED";

            } else if (percentageUsed.compareTo(
                    BigDecimal.valueOf(90)) >= 0) {

                status = "WARNING";

            } else {

                status = "OK";
            }

            budgetResponses.add(
                    new BudgetDashboardResponse(
                            budget.getCategory(),
                            budget.getAmount(),
                            spent,
                            remaining,
                            percentageUsed,
                            status
                    )
            );
        }

        // =========================
        // Spending by category
        // =========================

        List<Object[]> categoryResults =
                expenseRepository.getExpensesByCategoryAndDateRange(
                        userId,
                        currentMonthStart,
                        currentMonthEnd
                );

        Map<String, BigDecimal> spendingByCategory =
                new HashMap<>();

        for (Object[] row : categoryResults) {

            String category =
                    (String) row[0];

            BigDecimal amount =
                    (BigDecimal) row[1];

            spendingByCategory.put(
                    category,
                    amount
            );
        }

        // =========================
        // Build response
        // =========================

        DashboardResponse response =
                new DashboardResponse();

        response.setUserId(userId);

        response.setCurrentMonthIncome(
                currentMonthIncome
        );

        response.setCurrentMonthExpenses(
                currentMonthExpenses
        );

        response.setBalance(
                balance
        );

        response.setPreviousMonthExpenses(
                previousMonthExpenses
        );

        response.setExpenseChangePercentage(
                expenseChangePercentage
        );

        response.setMonthlySubscriptionCost(
                monthlySubscriptionCost
        );

        response.setSpendingByCategory(
                spendingByCategory
        );

        response.setBudgets(
                budgetResponses
        );

        response.setUpcomingSubscriptions(
                upcomingSubscriptions
        );

        return response;
    }

    // =========================
    // Calculate expense change
    // =========================

    private BigDecimal calculateExpenseChange(
            BigDecimal previousExpenses,
            BigDecimal currentExpenses) {

        if (previousExpenses.compareTo(
                BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        return currentExpenses
                .subtract(previousExpenses)
                .divide(
                        previousExpenses,
                        4,
                        RoundingMode.HALF_UP
                )
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }
}
