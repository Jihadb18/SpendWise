package com.spendwise.SpendWise.dto.subscription;

import com.spendwise.SpendWise.entity.SubscriptionFrequency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateSubscriptionRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Next payment date is required")
    private LocalDate nextPaymentDate;

    @NotNull(message = "Frequency is required")
    private SubscriptionFrequency frequency;

    @NotNull(message = "User ID is required")
    private Long userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(LocalDate nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public SubscriptionFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(SubscriptionFrequency frequency) {
        this.frequency = frequency;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}