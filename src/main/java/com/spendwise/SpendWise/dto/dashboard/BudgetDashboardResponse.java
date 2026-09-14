package com.spendwise.SpendWise.dto.dashboard;

import java.math.BigDecimal;

public class BudgetDashboardResponse {

    private String category;
    private BigDecimal budget;
    private BigDecimal spent;
    private BigDecimal remaining;
    private BigDecimal percentageUsed;
    private String status;

    public BudgetDashboardResponse() {
    }

    public BudgetDashboardResponse(
            String category,
            BigDecimal budget,
            BigDecimal spent,
            BigDecimal remaining,
            BigDecimal percentageUsed,
            String status) {
        this.category = category;
        this.budget = budget;
        this.spent = spent;
        this.remaining = remaining;
        this.percentageUsed = percentageUsed;
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }

    public void setRemaining(BigDecimal remaining) {
        this.remaining = remaining;
    }

    public BigDecimal getPercentageUsed() {
        return percentageUsed;
    }

    public void setPercentageUsed(BigDecimal percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}