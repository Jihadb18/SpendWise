package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.Subscription;
import com.spendwise.SpendWise.entity.SubscriptionFrequency;
import com.spendwise.SpendWise.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        subscriptionService =
                new SubscriptionService(subscriptionRepository);
    }

    @Test
    void shouldCreateSubscription() {

        Subscription subscription = new Subscription();
        subscription.setName("Netflix");
        subscription.setAmount(new BigDecimal("15.99"));
        subscription.setNextPaymentDate(
                LocalDate.of(2026, 10, 1)
        );
        subscription.setFrequency(
                SubscriptionFrequency.MONTHLY
        );

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Subscription result =
                subscriptionService.createSubscription(subscription);

        assertNotNull(result);
        assertEquals("Netflix", result.getName());
        assertEquals(
                new BigDecimal("15.99"),
                result.getAmount()
        );
        assertEquals(
                LocalDate.of(2026, 10, 1),
                result.getNextPaymentDate()
        );
        assertEquals(
                SubscriptionFrequency.MONTHLY,
                result.getFrequency()
        );

        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void shouldGetAllSubscriptions() {

        Subscription subscription1 = new Subscription();
        subscription1.setName("Netflix");

        Subscription subscription2 = new Subscription();
        subscription2.setName("Spotify");

        when(subscriptionRepository.findAll())
                .thenReturn(List.of(
                        subscription1,
                        subscription2
                ));

        List<Subscription> result =
                subscriptionService.getAllSubscriptions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(
                "Netflix",
                result.get(0).getName()
        );
        assertEquals(
                "Spotify",
                result.get(1).getName()
        );

        verify(subscriptionRepository).findAll();
    }

    @Test
    void shouldGetSubscriptionsByUserId() {

        Subscription subscription1 = new Subscription();
        subscription1.setName("Netflix");

        Subscription subscription2 = new Subscription();
        subscription2.setName("Spotify");

        when(subscriptionRepository.findByUserId(2L))
                .thenReturn(List.of(
                        subscription1,
                        subscription2
                ));

        List<Subscription> result =
                subscriptionService
                        .getSubscriptionsByUserId(2L);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                "Netflix",
                result.get(0).getName()
        );
        assertEquals(
                "Spotify",
                result.get(1).getName()
        );

        verify(subscriptionRepository)
                .findByUserId(2L);
    }

    @Test
    void shouldGetSubscriptionById() {

        Subscription subscription = new Subscription();
        subscription.setName("Netflix");
        subscription.setAmount(
                new BigDecimal("15.99")
        );

        when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(subscription));

        Subscription result =
                subscriptionService
                        .getSubscriptionById(1L);

        assertNotNull(result);
        assertEquals(
                "Netflix",
                result.getName()
        );
        assertEquals(
                new BigDecimal("15.99"),
                result.getAmount()
        );

        verify(subscriptionRepository)
                .findById(1L);
    }

    @Test
    void shouldReturnNullWhenSubscriptionNotFound() {

        when(subscriptionRepository.findById(999L))
                .thenReturn(Optional.empty());

        Subscription result =
                subscriptionService
                        .getSubscriptionById(999L);

        assertNull(result);

        verify(subscriptionRepository)
                .findById(999L);
    }

    @Test
    void shouldUpdateSubscription() {

        Subscription existingSubscription =
                new Subscription();

        existingSubscription.setName("Old Subscription");
        existingSubscription.setAmount(
                new BigDecimal("10.00")
        );
        existingSubscription.setNextPaymentDate(
                LocalDate.of(2026, 9, 1)
        );
        existingSubscription.setFrequency(
                SubscriptionFrequency.MONTHLY
        );

        Subscription updatedSubscription =
                new Subscription();

        updatedSubscription.setName("New Subscription");
        updatedSubscription.setAmount(
                new BigDecimal("25.00")
        );
        updatedSubscription.setNextPaymentDate(
                LocalDate.of(2026, 10, 15)
        );
        updatedSubscription.setFrequency(
                SubscriptionFrequency.YEARLY
        );

        when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(
                        existingSubscription
                ));

        when(subscriptionRepository.save(
                any(Subscription.class)
        )).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        Subscription result =
                subscriptionService.updateSubscription(
                        1L,
                        updatedSubscription
                );

        assertNotNull(result);

        assertEquals(
                "New Subscription",
                result.getName()
        );
        assertEquals(
                new BigDecimal("25.00"),
                result.getAmount()
        );
        assertEquals(
                LocalDate.of(2026, 10, 15),
                result.getNextPaymentDate()
        );
        assertEquals(
                SubscriptionFrequency.YEARLY,
                result.getFrequency()
        );

        verify(subscriptionRepository)
                .findById(1L);

        verify(subscriptionRepository)
                .save(existingSubscription);
    }

    @Test
    void shouldReturnNullWhenUpdatingNonExistingSubscription() {

        Subscription updatedSubscription =
                new Subscription();

        updatedSubscription.setName("Netflix");
        updatedSubscription.setAmount(
                new BigDecimal("15.99")
        );
        updatedSubscription.setNextPaymentDate(
                LocalDate.of(2026, 10, 1)
        );
        updatedSubscription.setFrequency(
                SubscriptionFrequency.MONTHLY
        );

        when(subscriptionRepository.findById(999L))
                .thenReturn(Optional.empty());

        Subscription result =
                subscriptionService.updateSubscription(
                        999L,
                        updatedSubscription
                );

        assertNull(result);

        verify(subscriptionRepository)
                .findById(999L);

        verify(
                subscriptionRepository,
                never()
        ).save(any(Subscription.class));
    }

    @Test
    void shouldDeleteSubscription() {

        doNothing()
                .when(subscriptionRepository)
                .deleteById(1L);

        subscriptionService.deleteSubscription(1L);

        verify(subscriptionRepository)
                .deleteById(1L);
    }

    @Test
    void shouldCalculateMonthlyCostIncludingYearlySubscriptions() {

        Long userId = 2L;

        // Monthly subscriptions = 20 + 10 = 30
        when(
                subscriptionRepository
                        .getMonthlySubscriptionCost(userId)
        ).thenReturn(
                new BigDecimal("30.00")
        );

        // Yearly subscriptions = 120 + 60 = 180
        // 180 / 12 = 15 per month
        Subscription yearly1 = new Subscription();
        yearly1.setAmount(
                new BigDecimal("120.00")
        );
        yearly1.setFrequency(
                SubscriptionFrequency.YEARLY
        );

        Subscription yearly2 = new Subscription();
        yearly2.setAmount(
                new BigDecimal("60.00")
        );
        yearly2.setFrequency(
                SubscriptionFrequency.YEARLY
        );

        when(subscriptionRepository.findByUserId(userId))
                .thenReturn(List.of(
                        yearly1,
                        yearly2
                ));

        BigDecimal result =
                subscriptionService.getMonthlyCost(userId);

        assertEquals(
                new BigDecimal("45.00"),
                result
        );

        verify(subscriptionRepository)
                .getMonthlySubscriptionCost(userId);

        verify(subscriptionRepository)
                .findByUserId(userId);
    }

    @Test
    void shouldGetUpcomingSubscriptions() {

        Long userId = 2L;

        Subscription subscription =
                new Subscription();

        subscription.setName("Netflix");
        subscription.setAmount(
                new BigDecimal("15.99")
        );
        subscription.setNextPaymentDate(
                LocalDate.now().plusDays(3)
        );
        subscription.setFrequency(
                SubscriptionFrequency.MONTHLY
        );

        when(
                subscriptionRepository
                        .findUpcomingSubscriptions(
                                eq(userId),
                                eq(LocalDate.now()),
                                eq(LocalDate.now().plusDays(7))
                        )
        ).thenReturn(List.of(subscription));

        List<Subscription> result =
                subscriptionService
                        .getUpcomingSubscriptions(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                "Netflix",
                result.get(0).getName()
        );

        verify(subscriptionRepository)
                .findUpcomingSubscriptions(
                        userId,
                        LocalDate.now(),
                        LocalDate.now().plusDays(7)
                );
    }
}