package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.income.CreateIncomeRequest;
import com.spendwise.SpendWise.entity.Income;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.IncomeService;

import jakarta.validation.Valid;

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
            @Valid @RequestBody CreateIncomeRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Income income = new Income();

        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setDate(request.getDate());
        income.setUser(user);

        return incomeService.createIncome(income);
    }

    @GetMapping
    public List<Income> getAllIncomes() {
        return incomeService.getAllIncomes();
    }

    @GetMapping("/user/{userId}")
    public List<Income> getIncomesByUserId(
            @PathVariable Long userId) {

        return incomeService.getIncomesByUserId(userId);
    }
    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalIncomeByUserId(
            @PathVariable Long userId) {

        return incomeService.getTotalIncomeByUserId(userId);
    }

    @GetMapping("/user/{userId}/current-month")
    public BigDecimal getCurrentMonthIncome(
            @PathVariable Long userId) {

        return incomeService.getCurrentMonthIncome(userId);
    }
    @GetMapping("/{id}")
    public Income getIncomeById(
            @PathVariable Long id) {

        return incomeService.getIncomeById(id);
    }

    @PutMapping("/{id}")
    public Income updateIncome(
            @PathVariable Long id,
            @RequestBody Income income) {

        return incomeService.updateIncome(id, income);
    }

    @DeleteMapping("/{id}")
    public void deleteIncome(
            @PathVariable Long id) {

        incomeService.deleteIncome(id);
    }
}