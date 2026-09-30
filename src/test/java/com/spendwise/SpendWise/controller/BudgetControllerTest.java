package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.budget.CreateBudgetRequest;
import com.spendwise.SpendWise.entity.Budget;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.exception.AccessDeniedException;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.BudgetService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BudgetController budgetController;

    private User user;
    private Budget budget;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Jihad");
        user.setEmail("jihad@example.com");

        budget = new Budget();
        budget.setId(1L);
        budget.setCategory("Food");
        budget.setAmount(new BigDecimal("1000"));
        budget.setMonth(LocalDate.of(2026, 9, 1));
        budget.setUser(user);

        when(authentication.getName())
                .thenReturn("jihad@example.com");

        when(userRepository.findByEmail("jihad@example.com"))
                .thenReturn(Optional.of(user));
    }

    @Test
    void shouldCreateBudget() {

        CreateBudgetRequest request =
                new CreateBudgetRequest();

        request.setCategory("Food");
        request.setAmount(new BigDecimal("1000"));
        request.setMonth(LocalDate.of(2026, 9, 1));

        when(budgetService.createBudget(any(Budget.class)))
                .thenReturn(budget);

        Budget result =
                budgetController.createBudget(
                        request,
                        authentication
                );

        assertNotNull(result);
        assertEquals("Food", result.getCategory());
        assertEquals(
                new BigDecimal("1000"),
                result.getAmount()
        );
        assertEquals(user, result.getUser());

        verify(budgetService)
                .createBudget(any(Budget.class));
    }

    @Test
    void shouldGetAllBudgets() {

        when(budgetService.getBudgetsByUserId(1L))
                .thenReturn(List.of(budget));

        List<Budget> result =
                budgetController.getAllBudgets(authentication);

        assertEquals(1, result.size());
        assertEquals(budget, result.get(0));

        verify(budgetService)
                .getBudgetsByUserId(1L);
    }

    @Test
    void shouldGetBudgetsByUserIdWhenOwner() {

        when(budgetService.getBudgetsByUserId(1L))
                .thenReturn(List.of(budget));

        List<Budget> result =
                budgetController.getBudgetsByUserId(
                        1L,
                        authentication
                );

        assertEquals(1, result.size());
        assertEquals(budget, result.get(0));
    }

    @Test
    void shouldRejectBudgetsByUserIdWhenNotOwner() {

        assertThrows(
                AccessDeniedException.class,
                () -> budgetController.getBudgetsByUserId(
                        2L,
                        authentication
                )
        );

        verify(budgetService, never())
                .getBudgetsByUserId(2L);
    }

    @Test
    void shouldGetBudgetsByUserAndMonth() {

        LocalDate month =
                LocalDate.of(2026, 9, 1);

        when(budgetService.getBudgetsByUserAndMonth(
                1L,
                month
        )).thenReturn(List.of(budget));

        List<Budget> result =
                budgetController.getBudgetsByUserAndMonth(
                        1L,
                        month,
                        authentication
                );

        assertEquals(1, result.size());
        assertEquals(budget, result.get(0));

        verify(budgetService)
                .getBudgetsByUserAndMonth(
                        1L,
                        month
                );
    }

    @Test
    void shouldGetBudgetByIdWhenOwner() {

        when(budgetService.getBudgetById(1L))
                .thenReturn(budget);

        Budget result =
                budgetController.getBudgetById(
                        1L,
                        authentication
                );

        assertEquals(budget, result);

        verify(budgetService)
                .getBudgetById(1L);
    }

    @Test
    void shouldUpdateBudgetWhenOwner() {

        Budget updatedBudget = new Budget();

        updatedBudget.setId(1L);
        updatedBudget.setCategory("Food");
        updatedBudget.setAmount(
                new BigDecimal("1200")
        );
        updatedBudget.setMonth(
                LocalDate.of(2026, 9, 1)
        );

        when(budgetService.getBudgetById(1L))
                .thenReturn(budget);

        when(budgetService.updateBudget(
                1L,
                updatedBudget
        )).thenReturn(updatedBudget);

        Budget result =
                budgetController.updateBudget(
                        1L,
                        updatedBudget,
                        authentication
                );

        assertEquals(updatedBudget, result);

        verify(budgetService)
                .updateBudget(
                        1L,
                        updatedBudget
                );
    }

    @Test
    void shouldDeleteBudgetWhenOwner() {

        when(budgetService.getBudgetById(1L))
                .thenReturn(budget);

        budgetController.deleteBudget(
                1L,
                authentication
        );

        verify(budgetService)
                .deleteBudget(1L);
    }

    @Test
    void shouldGetSpentForOwnedBudget() {

        BigDecimal spent =
                new BigDecimal("250");

        when(budgetService.getBudgetById(1L))
                .thenReturn(budget);

        when(budgetService.getSpent(
                1L,
                "Food",
                LocalDate.of(2026, 9, 1)
        )).thenReturn(spent);

        BigDecimal result =
                budgetController.getSpent(
                        1L,
                        authentication
                );

        assertEquals(spent, result);

        verify(budgetService)
                .getSpent(
                        1L,
                        "Food",
                        LocalDate.of(2026, 9, 1)
                );
    }

    @Test
    void shouldGetRemainingAndPercentageForOwnedBudget() {

        BigDecimal spent =
                new BigDecimal("250");

        BigDecimal remaining =
                new BigDecimal("750");

        BigDecimal percentage =
                new BigDecimal("25.00");

        when(budgetService.getBudgetById(1L))
                .thenReturn(budget);

        when(budgetService.getSpent(
                1L,
                "Food",
                LocalDate.of(2026, 9, 1)
        )).thenReturn(spent);

        when(budgetService.getRemaining(
                new BigDecimal("1000"),
                spent
        )).thenReturn(remaining);

        when(budgetService.getPercentageUsed(
                new BigDecimal("1000"),
                spent
        )).thenReturn(percentage);

        BigDecimal remainingResult =
                budgetController.getRemaining(
                        1L,
                        authentication
                );

        BigDecimal percentageResult =
                budgetController.getPercentageUsed(
                        1L,
                        authentication
                );

        assertEquals(remaining, remainingResult);
        assertEquals(percentage, percentageResult);

        verify(budgetService, times(2))
                .getBudgetById(1L);

        verify(budgetService, times(2))
                .getSpent(
                        1L,
                        "Food",
                        LocalDate.of(2026, 9, 1)
                );

        verify(budgetService)
                .getRemaining(
                        new BigDecimal("1000"),
                        spent
                );

        verify(budgetService)
                .getPercentageUsed(
                        new BigDecimal("1000"),
                        spent
                );
    }
}