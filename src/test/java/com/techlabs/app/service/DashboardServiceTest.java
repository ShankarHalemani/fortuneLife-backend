package com.techlabs.app.service;

import com.techlabs.app.dto.DashboardSummaryDto;
import com.techlabs.app.entity.InsurancePolicy;
import com.techlabs.app.entity.Payment;
import com.techlabs.app.enums.ClaimStatus;
import com.techlabs.app.enums.PolicyStatus;
import com.techlabs.app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private CommissionRepository commissionRepository;
    @Mock
    private InsurancePolicyRepository policyRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AgentRepository agentRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        // Common stubs
        when(paymentRepository.getTotalPaidAmount()).thenReturn(100000.0);
        when(claimRepository.getTotalApprovedClaimAmount()).thenReturn(30000.0);
        when(commissionRepository.getTotalCommissionPaid()).thenReturn(5000.0);
    }

    @Test
    void getDashboardSummary_calculatesNetBalanceCorrectly() {
        when(policyRepository.count()).thenReturn(50L);
        when(policyRepository.countByPolicyStatus(PolicyStatus.ACTIVE.name())).thenReturn(30L);
        when(customerRepository.count()).thenReturn(40L);
        when(agentRepository.count()).thenReturn(10L);
        when(claimRepository.countByClaimStatus(ClaimStatus.PENDING.name())).thenReturn(5L);
        when(claimRepository.countByClaimStatus(ClaimStatus.APPROVED.name())).thenReturn(8L);
        when(paymentRepository.getSchemeWiseIncome()).thenReturn(new ArrayList<>());
        when(paymentRepository.getMonthlyIncome(any())).thenReturn(new ArrayList<>());
        when(claimRepository.getMonthlyClaimExpenses(any())).thenReturn(new ArrayList<>());
        when(paymentRepository.findRecentPayments(any())).thenReturn(new ArrayList<>());

        DashboardSummaryDto summary = dashboardService.getDashboardSummary();

        assertEquals(100000.0, summary.getTotalIncome());
        assertEquals(35000.0, summary.getTotalExpenses()); // 30000 claims + 5000 commissions
        assertEquals(65000.0, summary.getNetBalance());    // 100000 - 35000
    }

    @Test
    void getDashboardSummary_returnsCounts() {
        when(policyRepository.count()).thenReturn(100L);
        when(policyRepository.countByPolicyStatus(PolicyStatus.ACTIVE.name())).thenReturn(60L);
        when(customerRepository.count()).thenReturn(80L);
        when(agentRepository.count()).thenReturn(15L);
        when(claimRepository.countByClaimStatus(ClaimStatus.PENDING.name())).thenReturn(12L);
        when(claimRepository.countByClaimStatus(ClaimStatus.APPROVED.name())).thenReturn(20L);
        when(paymentRepository.getSchemeWiseIncome()).thenReturn(new ArrayList<>());
        when(paymentRepository.getMonthlyIncome(any())).thenReturn(new ArrayList<>());
        when(claimRepository.getMonthlyClaimExpenses(any())).thenReturn(new ArrayList<>());
        when(paymentRepository.findRecentPayments(any())).thenReturn(new ArrayList<>());

        DashboardSummaryDto summary = dashboardService.getDashboardSummary();

        assertEquals(100L, summary.getTotalPolicies());
        assertEquals(60L, summary.getActivePolicies());
        assertEquals(80L, summary.getTotalCustomers());
        assertEquals(15L, summary.getTotalAgents());
        assertEquals(12L, summary.getPendingClaims());
        assertEquals(20L, summary.getApprovedClaims());
    }

    @Test
    void getDashboardSummary_schemeWiseIncomePopulated() {
        when(policyRepository.count()).thenReturn(10L);
        when(policyRepository.countByPolicyStatus(any())).thenReturn(5L);
        when(customerRepository.count()).thenReturn(10L);
        when(agentRepository.count()).thenReturn(3L);
        when(claimRepository.countByClaimStatus(any())).thenReturn(2L);
        when(paymentRepository.getMonthlyIncome(any())).thenReturn(new ArrayList<>());
        when(claimRepository.getMonthlyClaimExpenses(any())).thenReturn(new ArrayList<>());
        when(paymentRepository.findRecentPayments(any())).thenReturn(new ArrayList<>());

        List<Object[]> schemeData = new ArrayList<>();
        schemeData.add(new Object[]{"Life Insurance", 50000.0});
        schemeData.add(new Object[]{"Health Insurance", 30000.0});
        when(paymentRepository.getSchemeWiseIncome()).thenReturn(schemeData);

        DashboardSummaryDto summary = dashboardService.getDashboardSummary();

        assertEquals(2, summary.getSchemeWiseIncome().size());
        assertEquals(50000.0, summary.getSchemeWiseIncome().get("Life Insurance"));
        assertEquals(30000.0, summary.getSchemeWiseIncome().get("Health Insurance"));
    }

    @Test
    void getDashboardSummary_recentActivityFromPayments() {
        when(policyRepository.count()).thenReturn(10L);
        when(policyRepository.countByPolicyStatus(any())).thenReturn(5L);
        when(customerRepository.count()).thenReturn(10L);
        when(agentRepository.count()).thenReturn(3L);
        when(claimRepository.countByClaimStatus(any())).thenReturn(2L);
        when(paymentRepository.getSchemeWiseIncome()).thenReturn(new ArrayList<>());
        when(paymentRepository.getMonthlyIncome(any())).thenReturn(new ArrayList<>());
        when(claimRepository.getMonthlyClaimExpenses(any())).thenReturn(new ArrayList<>());

        Payment payment = new Payment();
        payment.setAmount(5000.0);
        payment.setPaymentDate(LocalDateTime.now());
        InsurancePolicy policy = new InsurancePolicy();
        payment.setInsurancePolicy(policy);
        when(paymentRepository.findRecentPayments(any())).thenReturn(List.of(payment));

        DashboardSummaryDto summary = dashboardService.getDashboardSummary();

        assertEquals(1, summary.getRecentActivity().size());
        assertEquals("PAYMENT", summary.getRecentActivity().get(0).getType());
        assertEquals(5000.0, summary.getRecentActivity().get(0).getAmount());
    }

    @Test
    void getDashboardSummary_zeroDataReturnsZeros() {
        when(paymentRepository.getTotalPaidAmount()).thenReturn(0.0);
        when(claimRepository.getTotalApprovedClaimAmount()).thenReturn(0.0);
        when(commissionRepository.getTotalCommissionPaid()).thenReturn(0.0);
        when(policyRepository.count()).thenReturn(0L);
        when(policyRepository.countByPolicyStatus(any())).thenReturn(0L);
        when(customerRepository.count()).thenReturn(0L);
        when(agentRepository.count()).thenReturn(0L);
        when(claimRepository.countByClaimStatus(any())).thenReturn(0L);
        when(paymentRepository.getSchemeWiseIncome()).thenReturn(new ArrayList<>());
        when(paymentRepository.getMonthlyIncome(any())).thenReturn(new ArrayList<>());
        when(claimRepository.getMonthlyClaimExpenses(any())).thenReturn(new ArrayList<>());
        when(paymentRepository.findRecentPayments(any())).thenReturn(new ArrayList<>());

        DashboardSummaryDto summary = dashboardService.getDashboardSummary();

        assertEquals(0.0, summary.getTotalIncome());
        assertEquals(0.0, summary.getTotalExpenses());
        assertEquals(0.0, summary.getNetBalance());
        assertTrue(summary.getSchemeWiseIncome().isEmpty());
        assertTrue(summary.getMonthlyTrends().isEmpty());
        assertTrue(summary.getRecentActivity().isEmpty());
    }
}
