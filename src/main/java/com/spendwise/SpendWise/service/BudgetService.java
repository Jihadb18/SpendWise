package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.Budget;
import com.spendwise.SpendWise.repository.BudgetRepository;
import com.spendwise.SpendWise.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            ExpenseRepository expenseRepository) {

        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public List<Budget> getBudgetsByUserAndMonth(
            Long userId,
            LocalDate month) {

        return budgetRepository.findByUserIdAndMonth(
                userId,
                month
        );
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public Budget updateBudget(Long id, Budget budget) {

        Budget existingBudget =
                budgetRepository.findById(id).orElse(null);

        if (existingBudget == null) {
            return null;
        }

        existingBudget.setCategory(budget.getCategory());
        existingBudget.setAmount(budget.getAmount());
        existingBudget.setMonth(budget.getMonth());

        return budgetRepository.save(existingBudget);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    public BigDecimal getSpent(
            Long userId,
            String category,
            LocalDate month) {

        LocalDate startDate = month.withDayOfMonth(1);

        LocalDate endDate = month.withDayOfMonth(
                month.lengthOfMonth()
        );

        BigDecimal spent =
                expenseRepository.getExpensesByCategoryAndDate(
                        userId,
                        category,
                        startDate,
                        endDate
                );

        return spent != null ? spent : BigDecimal.ZERO;
    }

    public BigDecimal getRemaining(
            BigDecimal budgetAmount,
            BigDecimal spent) {

        return budgetAmount.subtract(spent);
    }

    public BigDecimal getPercentageUsed(
            BigDecimal budgetAmount,
            BigDecimal spent) {

        if (budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return spent
                .divide(budgetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}