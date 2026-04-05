package com.techlabs.app.controller;

import com.techlabs.app.dto.PaymentDto;
import com.techlabs.app.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fortuneLife/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "Create Razorpay Order")
    @PostMapping("/create-order")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody PaymentDto paymentDto) {
        logger.info("Creating Razorpay order for policy: {}", paymentDto.getPolicyId());
        try {
            Map<String, Object> order = paymentService.createOrder(paymentDto);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Failed to create order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Verify Razorpay Payment and Save")
    @PostMapping("/verify")
    public ResponseEntity<Object> verifyPayment(@RequestBody Map<String, String> paymentData) {
        logger.info("Verifying Razorpay payment");
        try {
            Map<String, Object> result = paymentService.verifyAndSavePayment(
                    paymentData.get("razorpay_order_id"),
                    paymentData.get("razorpay_payment_id"),
                    paymentData.get("razorpay_signature"),
                    paymentData.get("policyId"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Payment verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get All Payments By Policy ID")
    @GetMapping("/policies/{policyId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByPolicyId(@PathVariable String policyId) {
        logger.info("Fetching all payments for policy with ID: {}", policyId);
        List<PaymentDto> paymentDtos = paymentService.getPaymentsByPolicyId(policyId);
        return new ResponseEntity<>(paymentDtos, HttpStatus.OK);
    }

    @Secured("ADMIN")
    @GetMapping("/revenue")
    public Double getRevenue(@RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        logger.info("Generating revenue");
        LocalDateTime startDate = LocalDateTime.parse(startDateStr);
        LocalDateTime endDate = LocalDateTime.parse(endDateStr);
        return paymentService.calculateTotalRevenue(startDate, endDate);
    }
}
