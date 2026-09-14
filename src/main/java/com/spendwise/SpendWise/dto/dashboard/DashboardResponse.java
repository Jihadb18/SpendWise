package com.spendwise.SpendWise.dto.dashboard;

import com.spendwise.SpendWise.dto.subscription.SubscriptionResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardResponse {

    private Long userId;

    private BigDecimal currentMonthIncome;

    private BigDecimal currentMonthExpenses;

    private BigDecimal balance;

    private BigDecimal previousMonthExpenses;

    private BigDecimal expenseChangePercentage;

    private BigDecimal monthlySubscriptionCost;

    private Map<String, BigDecimal> spendingByCategory;

    private List<SubscriptionResponse> upcomingSubscriptions;

    private List<BudgetDashboardResponse> budgets;

    public DashboardResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getCurrentMonthIncome() {
        return currentMonthIncome;
    }

    public void setCurrentMonthIncome(BigDecimal currentMonthIncome) {
        this.currentMonthIncome = currentMonthIncome;
    }

    public BigDecimal getCurrentMonthExpenses() {
        return currentMonthExpenses;
    }

    public void setCurrentMonthExpenses(BigDecimal currentMonthExpenses) {
        this.currentMonthExpenses = currentMonthExpenses;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getPreviousMonthExpenses() {
        return previousMonthExpenses;
    }

    public void setPreviousMonthExpenses(BigDecimal previousMonthExpenses) {
        this.previousMonthExpenses = previousMonthExpenses;
    }

    public BigDecimal getExpenseChangePercentage() {
        return expenseChangePercentage;
    }

    public void setExpenseChangePercentage(
            BigDecimal expenseChangePercentage) {

        this.expenseChangePercentage =
                expenseChangePercentage;
    }

    public BigDecimal getMonthlySubscriptionCost() {
        return monthlySubscriptionCost;
    }

    public void setMonthlySubscriptionCost(
            BigDecimal monthlySubscriptionCost) {

        this.monthlySubscriptionCost =
                monthlySubscriptionCost;
    }

    public Map<String, BigDecimal> getSpendingByCategory() {
        return spendingByCategory;
    }

    public void setSpendingByCategory(
            Map<String, BigDecimal> spendingByCategory) {

        this.spendingByCategory =
                spendingByCategory;
    }

    public List<SubscriptionResponse> getUpcomingSubscriptions() {
        return upcomingSubscriptions;
    }

    public void setUpcomingSubscriptions(
            List<SubscriptionResponse> upcomingSubscriptions) {

        this.upcomingSubscriptions =
                upcomingSubscriptions;
    }

    public List<BudgetDashboardResponse> getBudgets() {
        return budgets;
    }

    public void setBudgets(
            List<BudgetDashboardResponse> budgets) {

        this.budgets = budgets;
    }
}