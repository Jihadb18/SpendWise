package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.CreateExpenseRequest;
import com.spendwise.SpendWise.entity.Expense;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.ExpenseService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import com.spendwise.SpendWise.dto.ExpenseDashboardResponse;
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
    public Expense createExpense(@RequestBody CreateExpenseRequest request) {

        User user = userRepository.findById(request.getUserId())
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
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }
    @GetMapping("/user/{userId}")
    public List<Expense> getExpensesByUserId(@PathVariable Long userId) {
        return expenseService.getExpensesByUserId(userId);
    }
    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalExpensesByUserId(@PathVariable Long userId) {
        return expenseService.getTotalExpensesByUserId(userId);
    }
    @GetMapping("/user/{userId}/dashboard")
    public ExpenseDashboardResponse getDashboard(
            @PathVariable Long userId) {

        return expenseService.getDashboard(userId);
    }

    
    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(
            @PathVariable Long id,
            @RequestBody Expense expense) {
        return expenseService.updateExpense(id, expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }
}