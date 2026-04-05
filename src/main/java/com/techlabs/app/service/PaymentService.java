package com.techlabs.app.service;

import com.techlabs.app.dto.PaymentDto;
import com.techlabs.app.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    Map<String, Object> createOrder(PaymentDto paymentDto) throws Exception;

    Map<String, Object> verifyAndSavePayment(String razorpayOrderId, String razorpayPaymentId,
            String razorpaySignature, String policyId) throws Exception;

    List<PaymentDto> getPaymentsByPolicyId(String policyId);

    Double calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> getPaymentsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
