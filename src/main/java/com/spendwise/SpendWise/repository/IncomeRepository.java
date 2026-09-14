package com.spendwise.SpendWise.repository;

import com.spendwise.SpendWise.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserId(Long userId);

    @Query("""
           SELECT COALESCE(SUM(i.amount), 0)
           FROM Income i
           WHERE i.user.id = :userId
           """)
    BigDecimal getTotalIncomeByUserId(
            @Param("userId") Long userId);

    @Query("""
           SELECT COALESCE(SUM(i.amount), 0)
           FROM Income i
           WHERE i.user.id = :userId
           AND i.date BETWEEN :startDate AND :endDate
           """)
    BigDecimal getIncomeBetweenDates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}