package com.spendwise.SpendWise.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ExpenseDashboardResponse {

    private Long userId;
    private BigDecimal total;
    private Long count;
    private BigDecimal average;
    private BigDecimal highestExpense;
    private Map<String, BigDecimal> byCategory;
    private Map<String, BigDecimal> byMonth;

    public ExpenseDashboardResponse() {
    }

    public ExpenseDashboardResponse(
            Long userId,
            BigDecimal total,
            Long count,
            BigDecimal average,
            BigDecimal highestExpense,
            Map<String, BigDecimal> byCategory,
            Map<String, BigDecimal> byMonth) {

        this.userId = userId;
        this.total = total;
        this.count = count;
        this.average = average;
        this.highestExpense = highestExpense;
        this.byCategory = byCategory;
        this.byMonth = byMonth;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public BigDecimal getAverage() {
        return average;
    }

    public void setAverage(BigDecimal average) {
        this.average = average;
    }

    public BigDecimal getHighestExpense() {
        return highestExpense;
    }

    public void setHighestExpense(BigDecimal highestExpense) {
        this.highestExpense = highestExpense;
    }

    public Map<String, BigDecimal> getByCategory() {
        return byCategory;
    }

    public void setByCategory(Map<String, BigDecimal> byCategory) {
        this.byCategory = byCategory;
    }

    public Map<String, BigDecimal> getByMonth() {
        return byMonth;
    }

    public void setByMonth(Map<String, BigDecimal> byMonth) {
        this.byMonth = byMonth;
    }
}