package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.CreateExpenseRequest;
import com.spendwise.SpendWise.entity.Expense;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.exception.AccessDeniedException;
import com.spendwise.SpendWise.exception.ResourceNotFoundException;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseControllerTest {

    private ExpenseService expenseService;
    private UserRepository userRepository;
    private Authentication authentication;

    private ExpenseController expenseController;

    private User user;

    @BeforeEach
    void setUp() {

        expenseService =
                mock(ExpenseService.class);

        userRepository =
                mock(UserRepository.class);

        authentication =
                mock(Authentication.class);

        expenseController =
                new ExpenseController(
                        expenseService,
                        userRepository
                );

        user = new User();
        user.setId(1L);
        user.setName("Jihad");
        user.setEmail("jihad@example.com");

        when(authentication.getName())
                .thenReturn("jihad@example.com");

        when(userRepository.findByEmail(
                "jihad@example.com"
        )).thenReturn(
                Optional.of(user)
        );
    }

    @Test
    void shouldCreateExpense() {

        CreateExpenseRequest request =
                new CreateExpenseRequest();

        request.setDescription("Restaurant");
        request.setAmount(
                new BigDecimal("120.50")
        );
        request.setDate(
                LocalDate.of(2026, 9, 13)
        );
        request.setCategory("Food");

        Expense savedExpense =
                new Expense();

        savedExpense.setId(10L);
        savedExpense.setDescription("Restaurant");
        savedExpense.setAmount(
                new BigDecimal("120.50")
        );
        savedExpense.setDate(
                LocalDate.of(2026, 9, 13)
        );
        savedExpense.setCategory("Food");
        savedExpense.setUser(user);

        when(expenseService.createExpense(
                any(Expense.class)
        )).thenReturn(savedExpense);

        Expense result =
                expenseController.createExpense(
                        request,
                        authentication
                );

        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

        assertEquals(
                "Restaurant",
                result.getDescription()
        );

        assertEquals(
                new BigDecimal("120.50"),
                result.getAmount()
        );

        assertEquals(
                "Food",
                result.getCategory()
        );

        assertEquals(
                user,
                result.getUser()
        );

        verify(expenseService)
                .createExpense(
                        any(Expense.class)
                );
    }

    @Test
    void shouldGetAllExpensesForCurrentUser() {

        Expense expense =
                new Expense();

        expense.setId(10L);
        expense.setDescription("Restaurant");
        expense.setAmount(
                new BigDecimal("120.50")
        );
        expense.setUser(user);

        when(expenseService.getExpensesByUserId(1L))
                .thenReturn(
                        List.of(expense)
                );

        List<Expense> result =
                expenseController.getAllExpenses(
                        authentication
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                10L,
                result.get(0).getId()
        );

        verify(expenseService)
                .getExpensesByUserId(1L);
    }

    @Test
    void shouldGetExpensesByUserIdWhenOwner() {

        when(expenseService.getExpensesByUserId(1L))
                .thenReturn(List.of());

        List<Expense> result =
                expenseController.getExpensesByUserId(
                        1L,
                        authentication
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(expenseService)
                .getExpensesByUserId(1L);
    }

    @Test
    void shouldDenyAccessToAnotherUserExpenses() {

        assertThrows(
                AccessDeniedException.class,
                () -> expenseController.getExpensesByUserId(
                        2L,
                        authentication
                )
        );

        verify(expenseService, never())
                .getExpensesByUserId(anyLong());
    }

    @Test
    void shouldGetExpenseByIdWhenOwner() {

        Expense expense =
                new Expense();

        expense.setId(10L);
        expense.setDescription("Restaurant");
        expense.setAmount(
                new BigDecimal("120.50")
        );
        expense.setUser(user);

        when(expenseService.getExpenseById(10L))
                .thenReturn(expense);

        Expense result =
                expenseController.getExpenseById(
                        10L,
                        authentication
                );

        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

        assertEquals(
                "Restaurant",
                result.getDescription()
        );

        verify(expenseService)
                .getExpenseById(10L);
    }

    @Test
    void shouldDenyAccessToAnotherUserExpenseById() {

        User otherUser =
                new User();

        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        Expense expense =
                new Expense();

        expense.setId(10L);
        expense.setDescription("Other expense");
        expense.setAmount(
                new BigDecimal("500.00")
        );
        expense.setUser(otherUser);

        when(expenseService.getExpenseById(10L))
                .thenReturn(expense);

        assertThrows(
                AccessDeniedException.class,
                () -> expenseController.getExpenseById(
                        10L,
                        authentication
                )
        );
    }

    @Test
    void shouldUpdateExpenseWhenOwner() {

        Expense existingExpense =
                new Expense();

        existingExpense.setId(10L);
        existingExpense.setDescription("Old description");
        existingExpense.setAmount(
                new BigDecimal("100.00")
        );
        existingExpense.setUser(user);

        Expense updatedExpense =
                new Expense();

        updatedExpense.setId(10L);
        updatedExpense.setDescription("New description");
        updatedExpense.setAmount(
                new BigDecimal("150.00")
        );
        updatedExpense.setCategory("Food");
        updatedExpense.setUser(user);

        when(expenseService.getExpenseById(10L))
                .thenReturn(existingExpense);

        when(expenseService.updateExpense(
                eq(10L),
                any(Expense.class)
        )).thenReturn(updatedExpense);

        Expense result =
                expenseController.updateExpense(
                        10L,
                        updatedExpense,
                        authentication
                );

        assertNotNull(result);

        assertEquals(
                "New description",
                result.getDescription()
        );

        assertEquals(
                new BigDecimal("150.00"),
                result.getAmount()
        );

        verify(expenseService)
                .updateExpense(
                        eq(10L),
                        any(Expense.class)
                );
    }

    @Test
    void shouldDeleteExpenseWhenOwner() {

        Expense expense =
                new Expense();

        expense.setId(10L);
        expense.setDescription("Restaurant");
        expense.setUser(user);

        when(expenseService.getExpenseById(10L))
                .thenReturn(expense);

        expenseController.deleteExpense(
                10L,
                authentication
        );

        verify(expenseService)
                .deleteExpense(10L);
    }
}