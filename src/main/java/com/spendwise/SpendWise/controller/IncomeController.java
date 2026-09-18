package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.income.CreateIncomeRequest;
import com.spendwise.SpendWise.entity.Income;
import com.spendwise.SpendWise.entity.User;
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

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return incomeService.getIncomesByUserId(currentUser.getId());
    }

    @GetMapping("/user/{userId}")
    public List<Income> getIncomesByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return incomeService.getIncomesByUserId(userId);
    }

    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalIncomeByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return incomeService.getTotalIncomeByUserId(userId);
    }

    @GetMapping("/user/{userId}/current-month")
    public BigDecimal getCurrentMonthIncome(
            @PathVariable Long userId,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return incomeService.getCurrentMonthIncome(userId);
    }

    @GetMapping("/{id}")
    public Income getIncomeById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Income income = incomeService.getIncomeById(id);

        if (income == null) {
            throw new RuntimeException("Income not found");
        }

        if (income.getUser() == null) {
            throw new RuntimeException("Income has no owner");
        }

        if (!income.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return income;
    }

    @PutMapping("/{id}")
    public Income updateIncome(
            @PathVariable Long id,
            @RequestBody Income income,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Income existingIncome = incomeService.getIncomeById(id);

        if (existingIncome == null) {
            throw new RuntimeException("Income not found");
        }

        if (existingIncome.getUser() == null) {
            throw new RuntimeException("Income has no owner");
        }

        if (!existingIncome.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return incomeService.updateIncome(id, income);
    }

    @DeleteMapping("/{id}")
    public void deleteIncome(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Income existingIncome = incomeService.getIncomeById(id);

        if (existingIncome == null) {
            throw new RuntimeException("Income not found");
        }

        if (existingIncome.getUser() == null) {
            throw new RuntimeException("Income has no owner");
        }

        if (!existingIncome.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        incomeService.deleteIncome(id);
    }
}
