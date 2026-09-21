package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.Expense;
import com.spendwise.SpendWise.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.spendwise.SpendWise.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void shouldCreateExpense() {

        Expense expense = new Expense();

        expense.setDescription("Test Expense");
        expense.setAmount(new BigDecimal("100.00"));

        when(expenseRepository.save(expense))
                .thenReturn(expense);

        Expense result =
                expenseService.createExpense(expense);

        assertEquals("Test Expense",
                result.getDescription());

        assertEquals(
                new BigDecimal("100.00"),
                result.getAmount()
        );

        verify(expenseRepository)
                .save(expense);
    }
    @Test
    void shouldGetExpensesByUserId() {

        Long userId = 12L;

        Expense expense1 = new Expense();
        expense1.setDescription("Food");
        expense1.setAmount(new BigDecimal("80.00"));

        Expense expense2 = new Expense();
        expense2.setDescription("Transport");
        expense2.setAmount(new BigDecimal("40.00"));

        when(expenseRepository.findByUserId(userId))
                .thenReturn(java.util.List.of(expense1, expense2));

        var result =
                expenseService.getExpensesByUserId(userId);

        assertEquals(2, result.size());

        assertEquals(
                "Food",
                result.get(0).getDescription()
        );

        assertEquals(
                new BigDecimal("40.00"),
                result.get(1).getAmount()
        );

        verify(expenseRepository)
                .findByUserId(userId);
    }
    @Test
    void shouldGetTotalExpensesByUserId() {

        Long userId = 12L;

        BigDecimal expectedTotal =
                new BigDecimal("250.50");

        when(expenseRepository.getTotalExpensesByUserId(userId))
                .thenReturn(expectedTotal);

        BigDecimal result =
                expenseService.getTotalExpensesByUserId(userId);

        assertEquals(
                expectedTotal,
                result
        );

        verify(expenseRepository)
                .getTotalExpensesByUserId(userId);
    }
    @Test
    void shouldUpdateExpense() {

        Long expenseId = 1L;

        Expense existingExpense = new Expense();
        existingExpense.setDescription("Old Description");
        existingExpense.setAmount(new BigDecimal("50.00"));
        existingExpense.setCategory("Food");

        Expense updatedExpense = new Expense();
        updatedExpense.setDescription("New Description");
        updatedExpense.setAmount(new BigDecimal("100.00"));
        updatedExpense.setCategory("Shopping");

        when(expenseRepository.findById(expenseId))
                .thenReturn(java.util.Optional.of(existingExpense));

        when(expenseRepository.save(existingExpense))
                .thenReturn(existingExpense);

        Expense result =
                expenseService.updateExpense(
                        expenseId,
                        updatedExpense
                );

        assertEquals(
                "New Description",
                result.getDescription()
        );

        assertEquals(
                new BigDecimal("100.00"),
                result.getAmount()
        );

        assertEquals(
                "Shopping",
                result.getCategory()
        );

        verify(expenseRepository)
                .findById(expenseId);

        verify(expenseRepository)
                .save(existingExpense);
    }
    @Test
    void shouldDeleteExpense() {

        Long expenseId = 1L;

        when(expenseRepository.existsById(expenseId))
                .thenReturn(true);

        expenseService.deleteExpense(expenseId);

        verify(expenseRepository)
                .existsById(expenseId);

        verify(expenseRepository)
                .deleteById(expenseId);
    }
    @Test
    void shouldGetExpenseById() {

        Long expenseId = 1L;

        Expense expense = new Expense();
        expense.setDescription("Restaurant");
        expense.setAmount(new BigDecimal("80.00"));
        expense.setCategory("Food");

        when(expenseRepository.findById(expenseId))
                .thenReturn(java.util.Optional.of(expense));

        Expense result =
                expenseService.getExpenseById(expenseId);

        assertEquals(
                "Restaurant",
                result.getDescription()
        );

        assertEquals(
                new BigDecimal("80.00"),
                result.getAmount()
        );

        assertEquals(
                "Food",
                result.getCategory()
        );

        verify(expenseRepository)
                .findById(expenseId);
    }
    @Test
    void shouldThrowExceptionWhenExpenseNotFound() {

        Long expenseId = 999L;

        when(expenseRepository.findById(expenseId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.getExpenseById(expenseId)
        );

        verify(expenseRepository)
                .findById(expenseId);
    }
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingExpense() {

        Long expenseId = 999L;

        Expense updatedExpense = new Expense();

        when(expenseRepository.findById(expenseId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.updateExpense(
                        expenseId,
                        updatedExpense
                )
        );

        verify(expenseRepository)
                .findById(expenseId);

        verify(expenseRepository, never())
                .save(any(Expense.class));
    }
    @Test
    void shouldThrowExceptionWhenDeletingNonExistingExpense() {

        Long expenseId = 999L;

        when(expenseRepository.existsById(expenseId))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.deleteExpense(expenseId)
        );

        verify(expenseRepository)
                .existsById(expenseId);

        verify(expenseRepository, never())
                .deleteById(expenseId);
    }

}