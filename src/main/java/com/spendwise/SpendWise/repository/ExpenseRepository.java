package com.spendwise.SpendWise.repository;

import com.spendwise.SpendWise.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;



public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalExpensesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.user.id = :userId")
    Long countExpensesByUserId(@Param("userId") Long userId);

    @Query("""
           SELECT e.category, COALESCE(SUM(e.amount), 0)
           FROM Expense e
           WHERE e.user.id = :userId
           GROUP BY e.category
           """)

    List<Object[]> getExpensesByCategory(@Param("userId") Long userId);
    @Query("""
       SELECT FUNCTION('DATE_FORMAT', e.date, '%Y-%m'), COALESCE(SUM(e.amount), 0)
       FROM Expense e
       WHERE e.user.id = :userId
       GROUP BY FUNCTION('DATE_FORMAT', e.date, '%Y-%m')
       ORDER BY FUNCTION('DATE_FORMAT', e.date, '%Y-%m')
       """)
    List<Object[]> getExpensesByMonth(@Param("userId") Long userId);
}