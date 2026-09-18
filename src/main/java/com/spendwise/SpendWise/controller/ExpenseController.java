package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.CreateExpenseRequest;
import com.spendwise.SpendWise.entity.Expense;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.ExpenseService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import com.spendwise.SpendWise.dto.ExpenseDashboardResponse;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService,
                             UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Expense createExpense(
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense();

        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setCategory(request.getCategory());
        expense.setUser(user);

        return expenseService.createExpense(expense);
    }

    @GetMapping
    public List<Expense> getAllExpenses(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return expenseService.getExpensesByUserId(user.getId());
    }
    @GetMapping("/user/{userId}")
    public List<Expense> getExpensesByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return expenseService.getExpensesByUserId(userId);
    }
    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalExpensesByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return expenseService.getTotalExpensesByUserId(userId);
    }
    @GetMapping("/user/{userId}/dashboard")
    public ExpenseDashboardResponse getDashboard(
            @PathVariable Long userId,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return expenseService.getDashboard(userId);
    }


    @GetMapping("/{id}")
    public Expense getExpenseById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = expenseService.getExpenseById(id);

        if (expense == null) {
            throw new RuntimeException("Expense not found");
        }

        if (expense.getUser() == null) {
            throw new RuntimeException("Expense has no owner");
        }

        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return expense;
    }
    @PutMapping("/{id}")
    public Expense updateExpense(
            @PathVariable Long id,
            @RequestBody Expense expense,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense existingExpense = expenseService.getExpenseById(id);

        if (existingExpense == null) {
            throw new RuntimeException("Expense not found");
        }

        if (existingExpense.getUser() == null) {
            throw new RuntimeException("Expense has no owner");
        }

        if (!existingExpense.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return expenseService.updateExpense(id, expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense existingExpense = expenseService.getExpenseById(id);

        if (existingExpense == null) {
            throw new RuntimeException("Expense not found");
        }

        if (existingExpense.getUser() == null) {
            throw new RuntimeException("Expense has no owner");
        }

        if (!existingExpense.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        expenseService.deleteExpense(id);
    }
}