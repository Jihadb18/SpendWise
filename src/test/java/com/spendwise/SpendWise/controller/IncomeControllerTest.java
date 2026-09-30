package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.income.CreateIncomeRequest;
import com.spendwise.SpendWise.entity.Income;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.exception.AccessDeniedException;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.IncomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IncomeControllerTest {

    private IncomeService incomeService;
    private UserRepository userRepository;
    private Authentication authentication;

    private IncomeController incomeController;

    private User user;

    @BeforeEach
    void setUp() {

        incomeService =
                mock(IncomeService.class);

        userRepository =
                mock(UserRepository.class);

        authentication =
                mock(Authentication.class);

        incomeController =
                new IncomeController(
                        incomeService,
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
    void shouldCreateIncome() {

        CreateIncomeRequest request =
                new CreateIncomeRequest();

        request.setSource("Salary");
        request.setAmount(
                new BigDecimal("5000.00")
        );
        request.setDate(
                LocalDate.of(2026, 9, 1)
        );

        Income savedIncome =
                new Income();

        savedIncome.setId(10L);
        savedIncome.setSource("Salary");
        savedIncome.setAmount(
                new BigDecimal("5000.00")
        );
        savedIncome.setDate(
                LocalDate.of(2026, 9, 1)
        );
        savedIncome.setUser(user);

        when(incomeService.createIncome(
                any(Income.class)
        )).thenReturn(savedIncome);

        Income result =
                incomeController.createIncome(
                        request,
                        authentication
                );

        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

        assertEquals(
                "Salary",
                result.getSource()
        );

        assertEquals(
                new BigDecimal("5000.00"),
                result.getAmount()
        );

        assertEquals(
                user,
                result.getUser()
        );

        verify(incomeService)
                .createIncome(
                        any(Income.class)
                );
    }

    @Test
    void shouldGetAllIncomesForCurrentUser() {

        Income income =
                new Income();

        income.setId(10L);
        income.setSource("Salary");
        income.setAmount(
                new BigDecimal("5000.00")
        );
        income.setUser(user);

        when(incomeService.getIncomesByUserId(1L))
                .thenReturn(
                        List.of(income)
                );

        List<Income> result =
                incomeController.getAllIncomes(
                        authentication
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                10L,
                result.get(0).getId()
        );

        assertEquals(
                "Salary",
                result.get(0).getSource()
        );

        verify(incomeService)
                .getIncomesByUserId(1L);
    }

    @Test
    void shouldGetIncomesByUserIdWhenOwner() {

        when(incomeService.getIncomesByUserId(1L))
                .thenReturn(List.of());

        List<Income> result =
                incomeController.getIncomesByUserId(
                        1L,
                        authentication
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(incomeService)
                .getIncomesByUserId(1L);
    }

    @Test
    void shouldDenyAccessToAnotherUserIncomes() {

        assertThrows(
                AccessDeniedException.class,
                () -> incomeController.getIncomesByUserId(
                        2L,
                        authentication
                )
        );

        verify(incomeService, never())
                .getIncomesByUserId(anyLong());
    }

    @Test
    void shouldGetTotalIncomeWhenOwner() {

        when(incomeService.getTotalIncomeByUserId(1L))
                .thenReturn(
                        new BigDecimal("5000.00")
                );

        BigDecimal result =
                incomeController.getTotalIncomeByUserId(
                        1L,
                        authentication
                );

        assertEquals(
                new BigDecimal("5000.00"),
                result
        );

        verify(incomeService)
                .getTotalIncomeByUserId(1L);
    }

    @Test
    void shouldGetCurrentMonthIncomeWhenOwner() {

        when(incomeService.getCurrentMonthIncome(1L))
                .thenReturn(
                        new BigDecimal("5000.00")
                );

        BigDecimal result =
                incomeController.getCurrentMonthIncome(
                        1L,
                        authentication
                );

        assertEquals(
                new BigDecimal("5000.00"),
                result
        );

        verify(incomeService)
                .getCurrentMonthIncome(1L);
    }

    @Test
    void shouldGetIncomeByIdWhenOwner() {

        Income income =
                new Income();

        income.setId(10L);
        income.setSource("Salary");
        income.setAmount(
                new BigDecimal("5000.00")
        );
        income.setUser(user);

        when(incomeService.getIncomeById(10L))
                .thenReturn(income);

        Income result =
                incomeController.getIncomeById(
                        10L,
                        authentication
                );

        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

        assertEquals(
                "Salary",
                result.getSource()
        );

        verify(incomeService)
                .getIncomeById(10L);
    }

    @Test
    void shouldDenyAccessToAnotherUserIncomeById() {

        User otherUser =
                new User();

        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        Income income =
                new Income();

        income.setId(10L);
        income.setSource("Other salary");
        income.setAmount(
                new BigDecimal("7000.00")
        );
        income.setUser(otherUser);

        when(incomeService.getIncomeById(10L))
                .thenReturn(income);

        assertThrows(
                AccessDeniedException.class,
                () -> incomeController.getIncomeById(
                        10L,
                        authentication
                )
        );
    }

    @Test
    void shouldUpdateIncomeWhenOwner() {

        Income existingIncome =
                new Income();

        existingIncome.setId(10L);
        existingIncome.setSource("Old salary");
        existingIncome.setAmount(
                new BigDecimal("4000.00")
        );
        existingIncome.setUser(user);

        Income updatedIncome =
                new Income();

        updatedIncome.setId(10L);
        updatedIncome.setSource("New salary");
        updatedIncome.setAmount(
                new BigDecimal("5000.00")
        );
        updatedIncome.setUser(user);

        when(incomeService.getIncomeById(10L))
                .thenReturn(existingIncome);

        when(incomeService.updateIncome(
                eq(10L),
                any(Income.class)
        )).thenReturn(updatedIncome);

        Income result =
                incomeController.updateIncome(
                        10L,
                        updatedIncome,
                        authentication
                );

        assertNotNull(result);

        assertEquals(
                "New salary",
                result.getSource()
        );

        assertEquals(
                new BigDecimal("5000.00"),
                result.getAmount()
        );

        verify(incomeService)
                .updateIncome(
                        eq(10L),
                        any(Income.class)
                );
    }

    @Test
    void shouldDeleteIncomeWhenOwner() {

        Income income =
                new Income();

        income.setId(10L);
        income.setSource("Salary");
        income.setUser(user);

        when(incomeService.getIncomeById(10L))
                .thenReturn(income);

        incomeController.deleteIncome(
                10L,
                authentication
        );

        verify(incomeService)
                .deleteIncome(10L);
    }
}