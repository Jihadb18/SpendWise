package com.spendwise.SpendWise.controller;

import com.spendwise.SpendWise.dto.subscription.CreateSubscriptionRequest;
import com.spendwise.SpendWise.entity.Subscription;
import com.spendwise.SpendWise.entity.User;
import com.spendwise.SpendWise.exception.AccessDeniedException;
import com.spendwise.SpendWise.exception.ResourceNotFoundException;
import com.spendwise.SpendWise.repository.UserRepository;
import com.spendwise.SpendWise.service.SubscriptionService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
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
            @Valid @RequestBody CreateSubscriptionRequest request,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Subscription subscription = new Subscription();

        subscription.setName(request.getName());
        subscription.setAmount(request.getAmount());
        subscription.setNextPaymentDate(
                request.getNextPaymentDate());
        subscription.setFrequency(request.getFrequency());
        subscription.setUser(currentUser);

        return subscriptionService.createSubscription(
                subscription);
    }

    @GetMapping
    public List<Subscription> getAllSubscriptions(
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        return subscriptionService
                .getSubscriptionsByUserId(currentUser.getId());
    }

    @GetMapping("/user/{userId}")
    public List<Subscription> getSubscriptionsByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        checkUserIdOwnership(userId, currentUser);

        return subscriptionService
                .getSubscriptionsByUserId(userId);
    }

    @GetMapping("/{id}")
    public Subscription getSubscriptionById(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Subscription subscription =
                subscriptionService.getSubscriptionById(id);

        checkOwnership(subscription, currentUser);

        return subscription;
    }

    @PutMapping("/{id}")
    public Subscription updateSubscription(
            @PathVariable Long id,
            @RequestBody Subscription subscription,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Subscription existingSubscription =
                subscriptionService.getSubscriptionById(id);

        checkOwnership(existingSubscription, currentUser);

        return subscriptionService.updateSubscription(
                id,
                subscription);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscription(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Subscription existingSubscription =
                subscriptionService.getSubscriptionById(id);

        checkOwnership(existingSubscription, currentUser);

        subscriptionService.deleteSubscription(id);
    }

    @GetMapping("/user/{userId}/monthly-cost")
    public BigDecimal getMonthlyCost(
            @PathVariable Long userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        checkUserIdOwnership(userId, currentUser);

        return subscriptionService.getMonthlyCost(userId);
    }

    @GetMapping("/user/{userId}/upcoming")
    public List<Subscription> getUpcomingSubscriptions(
            @PathVariable Long userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        checkUserIdOwnership(userId, currentUser);

        return subscriptionService
                .getUpcomingSubscriptions(userId);
    }

    private User getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    private void checkUserIdOwnership(
            Long userId,
            User currentUser) {

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException(
                    "Access denied");
        }
    }

    private void checkOwnership(
            Subscription subscription,
            User currentUser) {

        if (subscription == null) {
            throw new ResourceNotFoundException(
                    "Subscription not found");
        }

        if (subscription.getUser() == null) {
            throw new ResourceNotFoundException(
                    "Subscription has no owner");
        }

        if (!subscription.getUser().getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "Access denied");
        }
    }
}
