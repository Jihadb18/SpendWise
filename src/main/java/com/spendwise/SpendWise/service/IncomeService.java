package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.Income;
import com.spendwise.SpendWise.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public Income createIncome(Income income) {
        return incomeRepository.save(income);
    }

    public List<Income> getAllIncomes() {
        return incomeRepository.findAll();
    }

    public List<Income> getIncomesByUserId(Long userId) {
        return incomeRepository.findByUserId(userId);
    }

    public Income getIncomeById(Long id) {
        return incomeRepository.findById(id).orElse(null);
    }

    public Income updateIncome(Long id, Income income) {

        Income existingIncome =
                incomeRepository.findById(id).orElse(null);

        if (existingIncome == null) {
            return null;
        }

        existingIncome.setSource(income.getSource());
        existingIncome.setAmount(income.getAmount());
        existingIncome.setDate(income.getDate());

        return incomeRepository.save(existingIncome);
    }

    public BigDecimal getIncomeBetweenDates(
            Long userId,
            LocalDate startDate,
            LocalDate endDate) {

        return incomeRepository.getIncomeBetweenDates(
                userId,
                startDate,
                endDate
        );
    }
    public BigDecimal getTotalIncomeByUserId(Long userId) {
        return incomeRepository.getTotalIncomeByUserId(userId);
    }

    public BigDecimal getCurrentMonthIncome(Long userId) {

        LocalDate today = LocalDate.now();

        LocalDate startDate = today.withDayOfMonth(1);

        LocalDate endDate = today.withDayOfMonth(
                today.lengthOfMonth()
        );

        return incomeRepository.getIncomeBetweenDates(
                userId,
                startDate,
                endDate
        );
    }
    public void deleteIncome(Long id) {
        incomeRepository.deleteById(id);
    }
}