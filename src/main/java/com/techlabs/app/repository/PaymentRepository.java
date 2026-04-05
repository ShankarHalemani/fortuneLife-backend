package com.techlabs.app.repository;

import com.techlabs.app.entity.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
        @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
        Double getTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
        List<Payment> findPaymentsWithinDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'PAID'")
        Double getTotalPaidAmount();

        @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PAID' ORDER BY p.paymentDate DESC")
        List<Payment> findRecentPayments(Pageable pageable);

        @Query("SELECT FUNCTION('to_char', p.paymentDate, 'YYYY-MM') AS month, COALESCE(SUM(p.amount), 0) AS total " +
                        "FROM Payment p WHERE p.paymentStatus = 'PAID' GROUP BY FUNCTION('to_char', p.paymentDate, 'YYYY-MM') "
                        +
                        "ORDER BY month DESC")
        List<Object[]> getMonthlyIncome(Pageable pageable);

        @Query("SELECT s.schemeName, COALESCE(SUM(p.amount), 0) " +
                        "FROM Payment p JOIN p.insurancePolicy ip JOIN ip.insuranceScheme s " +
                        "WHERE p.paymentStatus = 'PAID' GROUP BY s.schemeName")
        List<Object[]> getSchemeWiseIncome();
}
