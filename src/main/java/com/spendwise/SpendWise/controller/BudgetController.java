package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.budget.CreateBudgetRequest;
import com.spendwise.SpendWise.entity.Budget;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.exception.AccessDeniedException;
import com.spendwise.SpendWise.exception.ResourceNotFoundException;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.BudgetService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
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
            @Valid @RequestBody CreateBudgetRequest request,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Budget budget = new Budget();

        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setUser(currentUser);

        return budgetService.createBudget(budget);
    }

    @GetMapping
    public List<Budget> getAllBudgets(
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        return budgetService.getBudgetsByUserId(
                currentUser.getId());
    }

    @GetMapping("/user/{userId}")
    public List<Budget> getBudgetsByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        checkUserIdOwnership(userId, currentUser);

        return budgetService.getBudgetsByUserId(userId);
    }

    @GetMapping("/user/{userId}/month")
    public List<Budget> getBudgetsByUserAndMonth(
            @PathVariable Long userId,
            @RequestParam LocalDate month,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        checkUserIdOwnership(userId, currentUser);

        return budgetService.getBudgetsByUserAndMonth(
                userId,
                month
        );
    }

    @GetMapping("/{id}")
    public Budget getBudgetById(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Budget budget = budgetService.getBudgetById(id);

        checkOwnership(budget, currentUser);

        return budget;
    }

    @PutMapping("/{id}")
    public Budget updateBudget(
            @PathVariable Long id,
            @RequestBody Budget budget,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Budget existingBudget =
                budgetService.getBudgetById(id);

        checkOwnership(existingBudget, currentUser);

        return budgetService.updateBudget(id, budget);
    }

    @DeleteMapping("/{id}")
    public void deleteBudget(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Budget existingBudget =
                budgetService.getBudgetById(id);

        checkOwnership(existingBudget, currentUser);

        budgetService.deleteBudget(id);
    }

    @GetMapping("/{id}/spent")
    public BigDecimal getSpent(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Budget budget = budgetService.getBudgetById(id);

        checkOwnership(budget, currentUser);

        return budgetService.getSpent(
                currentUser.getId(),
                budget.getCategory(),
                budget.getMonth()
        );
    }

    @GetMapping("/{id}/remaining")
    public BigDecimal getRemaining(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Budget budget = budgetService.getBudgetById(id);

        checkOwnership(budget, currentUser);

        BigDecimal spent = budgetService.getSpent(
                currentUser.getId(),
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
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Budget budget = budgetService.getBudgetById(id);

        checkOwnership(budget, currentUser);

        BigDecimal spent = budgetService.getSpent(
                currentUser.getId(),
                budget.getCategory(),
                budget.getMonth()
        );

        return budgetService.getPercentageUsed(
                budget.getAmount(),
                spent
        );
    }

    private User getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    private void checkUserIdOwnership(
            Long userId,
            User currentUser) {

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException(
                    "Access denied");
        }
    }

    private void checkOwnership(
            Budget budget,
            User currentUser) {

        if (budget == null) {
            throw new ResourceNotFoundException(
                    "Budget not found");
        }

        if (budget.getUser() == null) {
            throw new ResourceNotFoundException(
                    "Budget has no owner");
        }

        if (!budget.getUser().getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "Access denied");
        }
    }
}
