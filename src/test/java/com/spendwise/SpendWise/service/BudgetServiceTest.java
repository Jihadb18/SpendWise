package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.Budget;
import com.spendwise.SpendWise.repository.BudgetRepository;
import com.spendwise.SpendWise.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    private BudgetRepository budgetRepository;
    private ExpenseRepository expenseRepository;
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetRepository = mock(BudgetRepository.class);
        expenseRepository = mock(ExpenseRepository.class);

        budgetService = new BudgetService(
                budgetRepository,
                expenseRepository
        );
    }

    @Test
    void shouldCreateBudget() {

        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setAmount(new BigDecimal("1000.00"));
        budget.setMonth(LocalDate.of(2026, 9, 1));

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Budget result = budgetService.createBudget(budget);

        assertNotNull(result);
        assertEquals("Food", result.getCategory());
        assertEquals(
                new BigDecimal("1000.00"),
                result.getAmount()
        );
        assertEquals(
                LocalDate.of(2026, 9, 1),
                result.getMonth()
        );

        verify(budgetRepository).save(budget);
    }

    @Test
    void shouldGetAllBudgets() {

        Budget budget1 = new Budget();
        budget1.setCategory("Food");
        budget1.setAmount(new BigDecimal("1000.00"));

        Budget budget2 = new Budget();
        budget2.setCategory("Transport");
        budget2.setAmount(new BigDecimal("500.00"));

        when(budgetRepository.findAll())
                .thenReturn(List.of(budget1, budget2));

        List<Budget> result = budgetService.getAllBudgets();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getCategory());
        assertEquals("Transport", result.get(1).getCategory());

        verify(budgetRepository).findAll();
    }

    @Test
    void shouldGetBudgetsByUserId() {

        Budget budget1 = new Budget();
        budget1.setCategory("Food");

        Budget budget2 = new Budget();
        budget2.setCategory("Education");

        when(budgetRepository.findByUserId(2L))
                .thenReturn(List.of(budget1, budget2));

        List<Budget> result =
                budgetService.getBudgetsByUserId(2L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getCategory());
        assertEquals("Education", result.get(1).getCategory());

        verify(budgetRepository).findByUserId(2L);
    }

    @Test
    void shouldGetBudgetsByUserAndMonth() {

        Long userId = 2L;
        LocalDate month =
                LocalDate.of(2026, 9, 1);

        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setAmount(new BigDecimal("1000.00"));
        budget.setMonth(month);

        when(budgetRepository.findByUserIdAndMonth(
                userId,
                month
        )).thenReturn(List.of(budget));

        List<Budget> result =
                budgetService.getBudgetsByUserAndMonth(
                        userId,
                        month
                );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategory());
        assertEquals(
                new BigDecimal("1000.00"),
                result.get(0).getAmount()
        );

        verify(budgetRepository)
                .findByUserIdAndMonth(userId, month);
    }

    @Test
    void shouldGetBudgetById() {

        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setAmount(new BigDecimal("1000.00"));

        when(budgetRepository.findById(1L))
                .thenReturn(Optional.of(budget));

        Budget result = budgetService.getBudgetById(1L);

        assertNotNull(result);
        assertEquals("Food", result.getCategory());
        assertEquals(
                new BigDecimal("1000.00"),
                result.getAmount()
        );

        verify(budgetRepository).findById(1L);
    }

    @Test
    void shouldReturnNullWhenBudgetNotFound() {

        when(budgetRepository.findById(999L))
                .thenReturn(Optional.empty());

        Budget result =
                budgetService.getBudgetById(999L);

        assertNull(result);

        verify(budgetRepository).findById(999L);
    }

    @Test
    void shouldUpdateBudget() {

        Budget existingBudget = new Budget();
        existingBudget.setCategory("Food");
        existingBudget.setAmount(new BigDecimal("800.00"));
        existingBudget.setMonth(
                LocalDate.of(2026, 8, 1)
        );

        Budget updatedBudget = new Budget();
        updatedBudget.setCategory("Education");
        updatedBudget.setAmount(new BigDecimal("1500.00"));
        updatedBudget.setMonth(
                LocalDate.of(2026, 9, 1)
        );

        when(budgetRepository.findById(1L))
                .thenReturn(Optional.of(existingBudget));

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Budget result =
                budgetService.updateBudget(
                        1L,
                        updatedBudget
                );

        assertNotNull(result);
        assertEquals(
                "Education",
                result.getCategory()
        );
        assertEquals(
                new BigDecimal("1500.00"),
                result.getAmount()
        );
        assertEquals(
                LocalDate.of(2026, 9, 1),
                result.getMonth()
        );

        verify(budgetRepository).findById(1L);
        verify(budgetRepository).save(existingBudget);
    }

    @Test
    void shouldReturnNullWhenUpdatingNonExistingBudget() {

        Budget updatedBudget = new Budget();
        updatedBudget.setCategory("Food");
        updatedBudget.setAmount(new BigDecimal("1000.00"));
        updatedBudget.setMonth(
                LocalDate.of(2026, 9, 1)
        );

        when(budgetRepository.findById(999L))
                .thenReturn(Optional.empty());

        Budget result =
                budgetService.updateBudget(
                        999L,
                        updatedBudget
                );

        assertNull(result);

        verify(budgetRepository).findById(999L);
        verify(
                budgetRepository,
                never()
        ).save(any(Budget.class));
    }

    @Test
    void shouldDeleteBudget() {

        doNothing()
                .when(budgetRepository)
                .deleteById(1L);

        budgetService.deleteBudget(1L);

        verify(budgetRepository).deleteById(1L);
    }

    @Test
    void shouldGetSpentForCategoryAndMonth() {

        Long userId = 2L;
        String category = "Food";
        LocalDate month =
                LocalDate.of(2026, 9, 1);

        BigDecimal expected =
                new BigDecimal("250.50");

        when(
                expenseRepository.getExpensesByCategoryAndDate(
                        eq(userId),
                        eq(category),
                        eq(LocalDate.of(2026, 9, 1)),
                        eq(LocalDate.of(2026, 9, 30))
                )
        ).thenReturn(expected);

        BigDecimal result =
                budgetService.getSpent(
                        userId,
                        category,
                        month
                );

        assertEquals(expected, result);

        verify(
                expenseRepository
        ).getExpensesByCategoryAndDate(
                userId,
                category,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );
    }

    @Test
    void shouldReturnZeroWhenSpentIsNull() {

        Long userId = 2L;
        String category = "Food";
        LocalDate month =
                LocalDate.of(2026, 9, 1);

        when(
                expenseRepository.getExpensesByCategoryAndDate(
                        eq(userId),
                        eq(category),
                        any(LocalDate.class),
                        any(LocalDate.class)
                )
        ).thenReturn(null);

        BigDecimal result =
                budgetService.getSpent(
                        userId,
                        category,
                        month
                );

        assertEquals(
                BigDecimal.ZERO,
                result
        );
    }

    @Test
    void shouldCalculateRemainingBudget() {

        BigDecimal budgetAmount =
                new BigDecimal("1000.00");

        BigDecimal spent =
                new BigDecimal("250.00");

        BigDecimal result =
                budgetService.getRemaining(
                        budgetAmount,
                        spent
                );

        assertEquals(
                new BigDecimal("750.00"),
                result
        );
    }

    @Test
    void shouldCalculatePercentageUsed() {

        BigDecimal budgetAmount =
                new BigDecimal("1000.00");

        BigDecimal spent =
                new BigDecimal("250.00");

        BigDecimal result =
                budgetService.getPercentageUsed(
                        budgetAmount,
                        spent
                );

        assertEquals(
                new BigDecimal("25.00"),
                result
        );
    }

    @Test
    void shouldReturnZeroPercentageWhenBudgetIsZero() {

        BigDecimal budgetAmount =
                BigDecimal.ZERO;

        BigDecimal spent =
                new BigDecimal("100.00");

        BigDecimal result =
                budgetService.getPercentageUsed(
                        budgetAmount,
                        spent
                );

        assertEquals(
                BigDecimal.ZERO,
                result
        );
    }
}