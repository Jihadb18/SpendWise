package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.subscription.CreateSubscriptionRequest;
import com.spendwise.SpendWise.entity.Subscription;
import com.spendwise.SpendWise.entity.SubscriptionFrequency;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.exception.AccessDeniedException;
import com.spendwise.SpendWise.exception.ResourceNotFoundException;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.SubscriptionService;

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
class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private User user;
    private Subscription subscription;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Jihad");
        user.setEmail("jihad@example.com");

        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setName("Netflix");
        subscription.setAmount(new BigDecimal("15.99"));
        subscription.setNextPaymentDate(
                LocalDate.of(2026, 10, 5)
        );
        subscription.setFrequency(
                SubscriptionFrequency.MONTHLY
        );
        subscription.setUser(user);

        when(authentication.getName())
                .thenReturn("jihad@example.com");

        when(userRepository.findByEmail("jihad@example.com"))
                .thenReturn(Optional.of(user));
    }

    @Test
    void shouldCreateSubscription() {

        CreateSubscriptionRequest request =
                new CreateSubscriptionRequest();

        request.setName("Netflix");
        request.setAmount(new BigDecimal("15.99"));
        request.setNextPaymentDate(
                LocalDate.of(2026, 10, 5)
        );
        request.setFrequency(
                SubscriptionFrequency.MONTHLY
        );

        when(subscriptionService.createSubscription(
                any(Subscription.class)
        )).thenReturn(subscription);

        Subscription result =
                subscriptionController.createSubscription(
                        request,
                        authentication
                );

        assertNotNull(result);
        assertEquals("Netflix", result.getName());
        assertEquals(
                new BigDecimal("15.99"),
                result.getAmount()
        );
        assertEquals(
                SubscriptionFrequency.MONTHLY,
                result.getFrequency()
        );
        assertEquals(user, result.getUser());

        verify(subscriptionService)
                .createSubscription(any(Subscription.class));
    }

    @Test
    void shouldGetAllSubscriptions() {

        when(subscriptionService.getSubscriptionsByUserId(1L))
                .thenReturn(List.of(subscription));

        List<Subscription> result =
                subscriptionController.getAllSubscriptions(
                        authentication
                );

        assertEquals(1, result.size());
        assertEquals(subscription, result.get(0));

        verify(subscriptionService)
                .getSubscriptionsByUserId(1L);
    }

    @Test
    void shouldGetSubscriptionsByUserIdWhenOwner() {

        when(subscriptionService.getSubscriptionsByUserId(1L))
                .thenReturn(List.of(subscription));

        List<Subscription> result =
                subscriptionController.getSubscriptionsByUserId(
                        1L,
                        authentication
                );

        assertEquals(1, result.size());
        assertEquals(subscription, result.get(0));
    }

    @Test
    void shouldRejectSubscriptionsByUserIdWhenNotOwner() {

        assertThrows(
                AccessDeniedException.class,
                () -> subscriptionController
                        .getSubscriptionsByUserId(
                                2L,
                                authentication
                        )
        );

        verify(subscriptionService, never())
                .getSubscriptionsByUserId(2L);
    }

    @Test
    void shouldGetSubscriptionByIdWhenOwner() {

        when(subscriptionService.getSubscriptionById(1L))
                .thenReturn(subscription);

        Subscription result =
                subscriptionController.getSubscriptionById(
                        1L,
                        authentication
                );

        assertEquals(subscription, result);

        verify(subscriptionService)
                .getSubscriptionById(1L);
    }

    @Test
    void shouldRejectSubscriptionByIdWhenNotOwner() {

        User otherUser = new User();
        otherUser.setId(2L);

        subscription.setUser(otherUser);

        when(subscriptionService.getSubscriptionById(1L))
                .thenReturn(subscription);

        assertThrows(
                AccessDeniedException.class,
                () -> subscriptionController
                        .getSubscriptionById(
                                1L,
                                authentication
                        )
        );
    }

    @Test
    void shouldUpdateSubscriptionWhenOwner() {

        Subscription updatedSubscription =
                new Subscription();

        updatedSubscription.setId(1L);
        updatedSubscription.setName("Spotify");
        updatedSubscription.setAmount(
                new BigDecimal("10.99")
        );
        updatedSubscription.setNextPaymentDate(
                LocalDate.of(2026, 10, 10)
        );
        updatedSubscription.setFrequency(
                SubscriptionFrequency.MONTHLY
        );

        when(subscriptionService.getSubscriptionById(1L))
                .thenReturn(subscription);

        when(subscriptionService.updateSubscription(
                1L,
                updatedSubscription
        )).thenReturn(updatedSubscription);

        Subscription result =
                subscriptionController.updateSubscription(
                        1L,
                        updatedSubscription,
                        authentication
                );

        assertEquals(updatedSubscription, result);

        verify(subscriptionService)
                .updateSubscription(
                        1L,
                        updatedSubscription
                );
    }

    @Test
    void shouldDeleteSubscriptionWhenOwner() {

        when(subscriptionService.getSubscriptionById(1L))
                .thenReturn(subscription);

        subscriptionController.deleteSubscription(
                1L,
                authentication
        );

        verify(subscriptionService)
                .deleteSubscription(1L);
    }

    @Test
    void shouldGetMonthlyCostWhenOwner() {

        BigDecimal monthlyCost =
                new BigDecimal("45.50");

        when(subscriptionService.getMonthlyCost(1L))
                .thenReturn(monthlyCost);

        BigDecimal result =
                subscriptionController.getMonthlyCost(
                        1L,
                        authentication
                );

        assertEquals(monthlyCost, result);

        verify(subscriptionService)
                .getMonthlyCost(1L);
    }

    @Test
    void shouldGetUpcomingSubscriptionsWhenOwner() {

        when(subscriptionService.getUpcomingSubscriptions(1L))
                .thenReturn(List.of(subscription));

        List<Subscription> result =
                subscriptionController.getUpcomingSubscriptions(
                        1L,
                        authentication
                );

        assertEquals(1, result.size());
        assertEquals(subscription, result.get(0));

        verify(subscriptionService)
                .getUpcomingSubscriptions(1L);
    }
}