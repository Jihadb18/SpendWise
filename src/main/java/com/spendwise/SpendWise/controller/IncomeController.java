package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.income.CreateIncomeRequest;
import com.spendwise.SpendWise.entity.Income;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.exception.AccessDeniedException;
import com.spendwise.SpendWise.exception.ResourceNotFoundException;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.IncomeService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    private final IncomeService incomeService;
    private final UserRepository userRepository;

    public IncomeController(
            IncomeService incomeService,
            UserRepository userRepository) {

        this.incomeService = incomeService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Income createIncome(
            @Valid @RequestBody CreateIncomeRequest request,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Income income = new Income();

        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setDate(request.getDate());
        income.setUser(currentUser);

        return incomeService.createIncome(income);
    }

    @GetMapping
    public List<Income> getAllIncomes(
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        return incomeService.getIncomesByUserId(
                currentUser.getId());
    }

    @GetMapping("/user/{userId}")
    public List<Income> getIncomesByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        checkUserIdOwnership(userId, currentUser);

        return incomeService.getIncomesByUserId(userId);
    }

    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalIncomeByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        checkUserIdOwnership(userId, currentUser);

        return incomeService.getTotalIncomeByUserId(userId);
    }

    @GetMapping("/user/{userId}/current-month")
    public BigDecimal getCurrentMonthIncome(
            @PathVariable Long userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        checkUserIdOwnership(userId, currentUser);

        return incomeService.getCurrentMonthIncome(userId);
    }

    @GetMapping("/{id}")
    public Income getIncomeById(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Income income = incomeService.getIncomeById(id);

        checkOwnership(income, currentUser);

        return income;
    }

    @PutMapping("/{id}")
    public Income updateIncome(
            @PathVariable Long id,
            @RequestBody Income income,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Income existingIncome =
                incomeService.getIncomeById(id);

        checkOwnership(existingIncome, currentUser);

        return incomeService.updateIncome(id, income);
    }

    @DeleteMapping("/{id}")
    public void deleteIncome(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Income existingIncome =
                incomeService.getIncomeById(id);

        checkOwnership(existingIncome, currentUser);

        incomeService.deleteIncome(id);
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
            Income income,
            User currentUser) {

        if (income == null) {
            throw new ResourceNotFoundException(
                    "Income not found");
        }

        if (income.getUser() == null) {
            throw new ResourceNotFoundException(
                    "Income has no owner");
        }

        if (!income.getUser().getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "Access denied");
        }
    }
}
