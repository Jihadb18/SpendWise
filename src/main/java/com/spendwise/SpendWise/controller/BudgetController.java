package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.budget.CreateBudgetRequest;
import com.spendwise.SpendWise.entity.Budget;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserRepository userRepository;

    public BudgetController(
            BudgetService budgetService,
            UserRepository userRepository) {

        this.budgetService = budgetService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Budget createBudget(
            @Valid @RequestBody CreateBudgetRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Budget budget = new Budget();

        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setUser(user);

        return budgetService.createBudget(budget);
    }

    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    @GetMapping("/user/{userId}")
    public List<Budget> getBudgetsByUserId(
            @PathVariable Long userId) {

        return budgetService.getBudgetsByUserId(userId);
    }

    @GetMapping("/user/{userId}/month")
    public List<Budget> getBudgetsByUserAndMonth(
            @PathVariable Long userId,
            @RequestParam LocalDate month) {

        return budgetService.getBudgetsByUserAndMonth(
                userId,
                month
        );
    }

    @GetMapping("/{id}")
    public Budget getBudgetById(
            @PathVariable Long id) {

        return budgetService.getBudgetById(id);
    }

    @PutMapping("/{id}")
    public Budget updateBudget(
            @PathVariable Long id,
            @RequestBody Budget budget) {

        return budgetService.updateBudget(id, budget);
    }

    @DeleteMapping("/{id}")
    public void deleteBudget(
            @PathVariable Long id) {

        budgetService.deleteBudget(id);
    }

    @GetMapping("/{id}/spent")
    public BigDecimal getSpent(
            @PathVariable Long id) {

        Budget budget = budgetService.getBudgetById(id);

        if (budget == null) {
            throw new RuntimeException("Budget not found");
        }

        return budgetService.getSpent(
                budget.getUser().getId(),
                budget.getCategory(),
                budget.getMonth()
        );
    }

    @GetMapping("/{id}/remaining")
    public BigDecimal getRemaining(
            @PathVariable Long id) {

        Budget budget = budgetService.getBudgetById(id);

        if (budget == null) {
            throw new RuntimeException("Budget not found");
        }

        BigDecimal spent = budgetService.getSpent(
                budget.getUser().getId(),
                budget.getCategory(),
                budget.getMonth()
        );

        return budgetService.getRemaining(
                budget.getAmount(),
                spent
        );
    }

    @GetMapping("/{id}/percentage")
    public BigDecimal getPercentageUsed(
            @PathVariable Long id) {

        Budget budget = budgetService.getBudgetById(id);

        if (budget == null) {
            throw new RuntimeException("Budget not found");
        }

        BigDecimal spent = budgetService.getSpent(
                budget.getUser().getId(),
                budget.getCategory(),
                budget.getMonth()
        );

        return budgetService.getPercentageUsed(
                budget.getAmount(),
                spent
        );
    }
}