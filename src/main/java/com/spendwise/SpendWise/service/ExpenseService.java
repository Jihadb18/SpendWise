package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.Expense;
import com.spendwise.SpendWise.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import com.spendwise.SpendWise.dto.ExpenseDashboardResponse;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }
    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findByUserId(userId);
    }
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }
    public BigDecimal getTotalExpensesByUserId(Long userId) {
        return expenseRepository.getTotalExpensesByUserId(userId);
    }
    public Expense updateExpense(Long id, Expense expense) {
        Expense existingExpense = expenseRepository.findById(id).orElse(null);

        if (existingExpense == null) {
            return null;
        }

        existingExpense.setDescription(expense.getDescription());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setDate(expense.getDate());
        existingExpense.setCategory(expense.getCategory());

        return expenseRepository.save(existingExpense);
    }
    public ExpenseDashboardResponse getDashboard(Long userId) {

        BigDecimal total = expenseRepository.getTotalExpensesByUserId(userId);

        Long count = expenseRepository.countExpensesByUserId(userId);

        List<Object[]> categoryResults =
                expenseRepository.getExpensesByCategory(userId);

        Map<String, BigDecimal> byCategory = new HashMap<>();

        for (Object[] row : categoryResults) {
            String category = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];

            byCategory.put(category, amount);
        }

        List<Object[]> monthResults =
                expenseRepository.getExpensesByMonth(userId);

        Map<String, BigDecimal> byMonth = new HashMap<>();

        for (Object[] row : monthResults) {
            String month = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];

            byMonth.put(month, amount);
        }

        return new ExpenseDashboardResponse(
                userId,
                total,
                count,
                byCategory,
                byMonth
        );
    }
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
}