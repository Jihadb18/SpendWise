package com.spendwise.SpendWise.service;

import com.spendwise.SpendWise.entity.Subscription;
import com.spendwise.SpendWise.entity.SubscriptionFrequency;
import com.spendwise.SpendWise.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository) {

        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription createSubscription(
            Subscription subscription) {

        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public List<Subscription> getSubscriptionsByUserId(
            Long userId) {

        return subscriptionRepository.findByUserId(userId);
    }

    public Subscription getSubscriptionById(Long id) {

        return subscriptionRepository
                .findById(id)
                .orElse(null);
    }

    public Subscription updateSubscription(
            Long id,
            Subscription subscription) {

        Subscription existingSubscription =
                subscriptionRepository
                        .findById(id)
                        .orElse(null);

        if (existingSubscription == null) {
            return null;
        }

        existingSubscription.setName(
                subscription.getName());

        existingSubscription.setAmount(
                subscription.getAmount());

        existingSubscription.setNextPaymentDate(
                subscription.getNextPaymentDate());

        existingSubscription.setFrequency(
                subscription.getFrequency());

        return subscriptionRepository.save(
                existingSubscription);
    }

    public void deleteSubscription(Long id) {

        subscriptionRepository.deleteById(id);
    }

    public BigDecimal getMonthlyCost(Long userId) {

        BigDecimal monthlyCost =
                subscriptionRepository
                        .getMonthlySubscriptionCost(userId);

        BigDecimal yearlyCost =
                subscriptionRepository
                        .findByUserId(userId)
                        .stream()
                        .filter(subscription ->
                                subscription.getFrequency()
                                        == SubscriptionFrequency.YEARLY)
                        .map(Subscription::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal yearlyAsMonthly =
                yearlyCost.divide(
                        BigDecimal.valueOf(12),
                        2,
                        RoundingMode.HALF_UP
                );

        return monthlyCost.add(yearlyAsMonthly);
    }

    public List<Subscription> getUpcomingSubscriptions(
            Long userId) {

        LocalDate today = LocalDate.now();

        LocalDate nextWeek = today.plusDays(7);

        return subscriptionRepository
                .findUpcomingSubscriptions(
                        userId,
                        today,
                        nextWeek
                );
    }
}