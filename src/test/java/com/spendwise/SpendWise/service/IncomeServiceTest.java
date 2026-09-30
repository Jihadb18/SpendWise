package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.Income;
import com.spendwise.SpendWise.repository.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IncomeServiceTest {

    private IncomeRepository incomeRepository;
    private IncomeService incomeService;

    @BeforeEach
    void setUp() {
        incomeRepository = mock(IncomeRepository.class);
        incomeService = new IncomeService(incomeRepository);
    }

    @Test
    void shouldCreateIncome() {

        Income income = new Income();
        income.setSource("Salary");
        income.setAmount(new BigDecimal("5000.00"));
        income.setDate(LocalDate.of(2026, 9, 15));

        when(incomeRepository.save(any(Income.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Income result = incomeService.createIncome(income);

        assertNotNull(result);
        assertEquals("Salary", result.getSource());
        assertEquals(
                new BigDecimal("5000.00"),
                result.getAmount()
        );
        assertEquals(
                LocalDate.of(2026, 9, 15),
                result.getDate()
        );

        verify(incomeRepository).save(income);
    }

    @Test
    void shouldGetAllIncomes() {

        Income income1 = new Income();
        income1.setSource("Salary");
        income1.setAmount(new BigDecimal("5000.00"));

        Income income2 = new Income();
        income2.setSource("Freelance");
        income2.setAmount(new BigDecimal("1500.00"));

        when(incomeRepository.findAll())
                .thenReturn(List.of(income1, income2));

        List<Income> result = incomeService.getAllIncomes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Salary", result.get(0).getSource());
        assertEquals("Freelance", result.get(1).getSource());

        verify(incomeRepository).findAll();
    }

    @Test
    void shouldGetIncomesByUserId() {

        Income income1 = new Income();
        income1.setSource("Salary");
        income1.setAmount(new BigDecimal("5000.00"));

        Income income2 = new Income();
        income2.setSource("Freelance");
        income2.setAmount(new BigDecimal("1000.00"));

        when(incomeRepository.findByUserId(2L))
                .thenReturn(List.of(income1, income2));

        List<Income> result =
                incomeService.getIncomesByUserId(2L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Salary", result.get(0).getSource());
        assertEquals("Freelance", result.get(1).getSource());

        verify(incomeRepository).findByUserId(2L);
    }

    @Test
    void shouldGetIncomeById() {

        Income income = new Income();
        income.setSource("Salary");
        income.setAmount(new BigDecimal("5000.00"));

        when(incomeRepository.findById(1L))
                .thenReturn(Optional.of(income));

        Income result = incomeService.getIncomeById(1L);

        assertNotNull(result);
        assertEquals("Salary", result.getSource());
        assertEquals(
                new BigDecimal("5000.00"),
                result.getAmount()
        );

        verify(incomeRepository).findById(1L);
    }

    @Test
    void shouldReturnNullWhenIncomeNotFound() {

        when(incomeRepository.findById(999L))
                .thenReturn(Optional.empty());

        Income result = incomeService.getIncomeById(999L);

        assertNull(result);

        verify(incomeRepository).findById(999L);
    }

    @Test
    void shouldUpdateIncome() {

        Income existingIncome = new Income();
        existingIncome.setSource("Old Salary");
        existingIncome.setAmount(new BigDecimal("4000.00"));
        existingIncome.setDate(LocalDate.of(2026, 9, 1));

        Income updatedIncome = new Income();
        updatedIncome.setSource("New Salary");
        updatedIncome.setAmount(new BigDecimal("5500.00"));
        updatedIncome.setDate(LocalDate.of(2026, 9, 20));

        when(incomeRepository.findById(1L))
                .thenReturn(Optional.of(existingIncome));

        when(incomeRepository.save(any(Income.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Income result =
                incomeService.updateIncome(1L, updatedIncome);

        assertNotNull(result);
        assertEquals("New Salary", result.getSource());
        assertEquals(
                new BigDecimal("5500.00"),
                result.getAmount()
        );
        assertEquals(
                LocalDate.of(2026, 9, 20),
                result.getDate()
        );

        verify(incomeRepository).findById(1L);
        verify(incomeRepository).save(existingIncome);
    }

    @Test
    void shouldReturnNullWhenUpdatingNonExistingIncome() {

        Income updatedIncome = new Income();
        updatedIncome.setSource("New Salary");
        updatedIncome.setAmount(new BigDecimal("5500.00"));
        updatedIncome.setDate(LocalDate.of(2026, 9, 20));

        when(incomeRepository.findById(999L))
                .thenReturn(Optional.empty());

        Income result =
                incomeService.updateIncome(999L, updatedIncome);

        assertNull(result);

        verify(incomeRepository).findById(999L);
        verify(incomeRepository, never()).save(any(Income.class));
    }

    @Test
    void shouldGetIncomeBetweenDates() {

        Long userId = 2L;

        LocalDate startDate =
                LocalDate.of(2026, 9, 1);

        LocalDate endDate =
                LocalDate.of(2026, 9, 30);

        BigDecimal expected =
                new BigDecimal("6500.00");

        when(incomeRepository.getIncomeBetweenDates(
                userId,
                startDate,
                endDate
        )).thenReturn(expected);

        BigDecimal result =
                incomeService.getIncomeBetweenDates(
                        userId,
                        startDate,
                        endDate
                );

        assertEquals(expected, result);

        verify(incomeRepository).getIncomeBetweenDates(
                userId,
                startDate,
                endDate
        );
    }

    @Test
    void shouldGetTotalIncomeByUserId() {

        Long userId = 2L;

        BigDecimal expected =
                new BigDecimal("8000.00");

        when(incomeRepository.getTotalIncomeByUserId(userId))
                .thenReturn(expected);

        BigDecimal result =
                incomeService.getTotalIncomeByUserId(userId);

        assertEquals(expected, result);

        verify(incomeRepository)
                .getTotalIncomeByUserId(userId);
    }

    @Test
    void shouldGetCurrentMonthIncome() {

        Long userId = 2L;

        BigDecimal expected =
                new BigDecimal("6500.00");

        when(incomeRepository.getIncomeBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(expected);

        BigDecimal result =
                incomeService.getCurrentMonthIncome(userId);

        assertEquals(expected, result);

        verify(incomeRepository).getIncomeBetweenDates(
                eq(userId),
                any(LocalDate.class),
                any(LocalDate.class)
        );
    }

    @Test
    void shouldDeleteIncome() {

        doNothing()
                .when(incomeRepository)
                .deleteById(1L);

        incomeService.deleteIncome(1L);

        verify(incomeRepository).deleteById(1L);
    }
}