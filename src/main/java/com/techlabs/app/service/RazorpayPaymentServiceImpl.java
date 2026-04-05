package com.techlabs.app.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.techlabs.app.dto.PaymentDto;
import com.techlabs.app.entity.*;
import com.techlabs.app.enums.CommissionType;
import com.techlabs.app.enums.PaymentStatus;
import com.techlabs.app.enums.PaymentType;
import com.techlabs.app.exception.FortuneLifeException;
import com.techlabs.app.mapper.PaymentMapper;
import com.techlabs.app.repository.AgentRepository;
import com.techlabs.app.repository.CommissionRepository;
import com.techlabs.app.repository.InsurancePolicyRepository;
import com.techlabs.app.repository.PaymentRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RazorpayPaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayPaymentServiceImpl.class);

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    private InsurancePolicyRepository policyRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public Map<String, Object> createOrder(PaymentDto paymentDto) throws Exception {
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        // Validate policy exists
        InsurancePolicy policy = policyRepository.findById(paymentDto.getPolicyId())
                .orElseThrow(() -> new FortuneLifeException(
                        "Policy with ID : " + paymentDto.getPolicyId() + " cannot be found"));

        // Validate payment amount won't exceed total policy amount
        double totalAfterPayment = paymentDto.getAmount() + policy.getPaidPolicyAmountTillDate();
        if (policy.getTotalPolicyAmount() < totalAfterPayment) {
            throw new FortuneLifeException("Paid amount till date would exceed total policy amount");
        }

        // Amount in paise (Razorpay expects smallest currency unit)
        int amountInPaise = (int) (paymentDto.getTotalPayment() * 100);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "policy_" + paymentDto.getPolicyId());

        Order order = client.orders.create(orderRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("keyId", razorpayKeyId);

        logger.info("Razorpay order created: {} for policy: {}", order.get("id"), paymentDto.getPolicyId());
        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> verifyAndSavePayment(String razorpayOrderId, String razorpayPaymentId,
                                                     String razorpaySignature, String policyId) throws Exception {
        // Verify payment signature
        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);

        boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
        if (!isValid) {
            throw new FortuneLifeException("Payment verification failed. Invalid signature.");
        }

        // Fetch order details to get amount
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        com.razorpay.Payment rzpPayment = client.payments.fetch(razorpayPaymentId);

        InsurancePolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new FortuneLifeException("Policy with ID : " + policyId + " cannot be found"));

        int amountInPaise = rzpPayment.get("amount");
        double totalPayment = amountInPaise / 100.0;
        String method = rzpPayment.get("method");

        // Create and save payment
        Payment payment = new Payment();
        payment.setPaymentType(mapRazorpayMethod(method));
        payment.setTotalPayment(totalPayment);
        payment.setAmount(totalPayment); // Will be refined if tax info is available
        payment.setTax(0.0);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setInsurancePolicy(policy);
        payment.setPaymentStatus(PaymentStatus.PAID.name());

        // Update policy paid amount
        double totalAmountPaidTillDate = payment.getAmount() + policy.getPaidPolicyAmountTillDate();
        policy.setPaidPolicyAmountTillDate(totalAmountPaidTillDate);

        if (Objects.equals(policy.getTotalPolicyAmount(), policy.getPaidPolicyAmountTillDate()) &&
                (policy.getMaturityDate().isEqual(LocalDate.now()) || policy.getMaturityDate().isBefore(LocalDate.now()))) {
            policy.setPolicyStatus("COMPLETE");
        }

        policy.getPayments().add(payment);
        paymentRepository.save(payment);

        // Handle agent commission
        processAgentCommission(policy, payment.getAmount());

        logger.info("Payment verified and saved. Razorpay Payment ID: {}", razorpayPaymentId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("paymentId", razorpayPaymentId);
        response.put("message", "Payment verified and recorded successfully");
        return response;
    }

    private void processAgentCommission(InsurancePolicy policy, double paymentAmount) {
        Agent agent = policy.getAgent();
        if (agent != null && agent.getActive() && agent.getVerified()) {
            InsuranceScheme insuranceScheme = policyRepository.findInsuranceSchemeByPolicyId(policy.getId());
            double installmentRatio = insuranceScheme.getSchemeDetails().getInstallmentCommissionRatio();
            double commissionAmount = Double.parseDouble(
                    String.format("%.2f", (paymentAmount * installmentRatio) / 100));

            Commission commission = new Commission();
            commission.setPolicyId(policy.getId());
            commission.setCommissionType(CommissionType.INSTALMENT.name());
            commission.setAmount(commissionAmount);
            commission.setAgent(agent);

            Commission savedCommission = commissionRepository.save(commission);
            agent.getCommissions().add(savedCommission);
            agent.setTotalCommission(agent.getTotalCommission() + commissionAmount);
            agentRepository.save(agent);
        }
    }

    private String mapRazorpayMethod(String method) {
        return switch (method) {
            case "upi" -> PaymentType.UPI.name();
            case "netbanking" -> PaymentType.NETBANKING.name();
            case "wallet" -> PaymentType.WALLET.name();
            case "card" -> PaymentType.CREDIT_CARD.name();
            default -> PaymentType.DEBIT_CARD.name();
        };
    }

    @Override
    public List<PaymentDto> getPaymentsByPolicyId(String policyId) {
        InsurancePolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new FortuneLifeException("No Policy Found With ID: " + policyId));
        return policy.getPayments().stream()
                .map(paymentMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.getTotalRevenue(startDate, endDate);
    }

    @Override
    public List<Payment> getPaymentsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findPaymentsWithinDateRange(startDate, endDate);
    }
}
