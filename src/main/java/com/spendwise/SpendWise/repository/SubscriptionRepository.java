package com.spendwise.SpendWise.repository;

import com.spendwise.SpendWise.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long userId);

    @Query("""
           SELECT COALESCE(SUM(s.amount), 0)
           FROM Subscription s
           WHERE s.user.id = :userId
           AND s.frequency = com.spendwise.SpendWise.entity.SubscriptionFrequency.MONTHLY
           """)
    BigDecimal getMonthlySubscriptionCost(
            @Param("userId") Long userId);

    @Query("""
           SELECT s
           FROM Subscription s
           WHERE s.user.id = :userId
           AND s.nextPaymentDate BETWEEN :startDate AND :endDate
           ORDER BY s.nextPaymentDate ASC
           """)
    List<Subscription> findUpcomingSubscriptions(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}