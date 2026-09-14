package com.spendwise.SpendWise.dto.subscription;

import com.spendwise.SpendWise.entity.SubscriptionFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SubscriptionResponse {

    private Long id;
    private String name;
    private BigDecimal amount;
    private LocalDate nextPaymentDate;
    private SubscriptionFrequency frequency;

    public SubscriptionResponse() {
    }

    public SubscriptionResponse(
            Long id,
            String name,
            BigDecimal amount,
            LocalDate nextPaymentDate,
            SubscriptionFrequency frequency) {

        this.id = id;
        this.name = name;
        this.amount = amount;
        this.nextPaymentDate = nextPaymentDate;
        this.frequency = frequency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}