package com.spendwise.SpendWise.repository;

import com.spendwise.SpendWise.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndMonth(
            Long userId,
            LocalDate month
    );
}