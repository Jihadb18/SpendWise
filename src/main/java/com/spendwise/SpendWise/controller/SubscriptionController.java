package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.subscription.CreateSubscriptionRequest;
import com.spendwise.SpendWise.entity.Subscription;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public SubscriptionController(
            SubscriptionService subscriptionService,
            UserRepository userRepository) {

        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Subscription createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Subscription subscription = new Subscription();

        subscription.setName(request.getName());
        subscription.setAmount(request.getAmount());
        subscription.setNextPaymentDate(
                request.getNextPaymentDate());
        subscription.setFrequency(request.getFrequency());
        subscription.setUser(user);

        return subscriptionService.createSubscription(
                subscription);
    }

    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }

    @GetMapping("/user/{userId}")
    public List<Subscription> getSubscriptionsByUserId(
            @PathVariable Long userId) {

        return subscriptionService
                .getSubscriptionsByUserId(userId);
    }

    @GetMapping("/{id}")
    public Subscription getSubscriptionById(
            @PathVariable Long id) {

        return subscriptionService
                .getSubscriptionById(id);
    }

    @PutMapping("/{id}")
    public Subscription updateSubscription(
            @PathVariable Long id,
            @RequestBody Subscription subscription) {

        return subscriptionService.updateSubscription(
                id,
                subscription);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscription(
            @PathVariable Long id) {

        subscriptionService.deleteSubscription(id);
    }

    @GetMapping("/user/{userId}/monthly-cost")
    public BigDecimal getMonthlyCost(
            @PathVariable Long userId) {

        return subscriptionService.getMonthlyCost(userId);
    }

    @GetMapping("/user/{userId}/upcoming")
    public List<Subscription> getUpcomingSubscriptions(
            @PathVariable Long userId) {

        return subscriptionService
                .getUpcomingSubscriptions(userId);
    }
}