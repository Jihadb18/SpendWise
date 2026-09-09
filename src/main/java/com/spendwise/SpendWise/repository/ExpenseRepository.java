package com.spendwise.SpendWise.repository;

import com.spendwise.SpendWise.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}