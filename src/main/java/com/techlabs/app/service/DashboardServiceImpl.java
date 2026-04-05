package com.techlabs.app.service;

import com.techlabs.app.dto.DashboardSummaryDto;
import com.techlabs.app.dto.MonthlyTrendDto;
import com.techlabs.app.dto.RecentActivityDto;
import com.techlabs.app.entity.Payment;
import com.techlabs.app.enums.ClaimStatus;
import com.techlabs.app.enums.PolicyStatus;
import com.techlabs.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private InsurancePolicyRepository policyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Override
    public DashboardSummaryDto getDashboardSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto();

        // Income: total premium payments received
        Double totalIncome = paymentRepository.getTotalPaidAmount();

        // Expenses: approved claims + commissions
        Double totalClaims = claimRepository.getTotalApprovedClaimAmount();
        Double totalCommissions = commissionRepository.getTotalCommissionPaid();
        Double totalExpenses = totalClaims + totalCommissions;

        summary.setTotalIncome(totalIncome);
        summary.setTotalExpenses(totalExpenses);
        summary.setNetBalance(totalIncome - totalExpenses);

        // Counts
        summary.setTotalPolicies(policyRepository.count());
        summary.setActivePolicies(policyRepository.countByPolicyStatus(PolicyStatus.ACTIVE.name()));
        summary.setTotalCustomers(customerRepository.count());
        summary.setTotalAgents(agentRepository.count());
        summary.setPendingClaims(claimRepository.countByClaimStatus(ClaimStatus.PENDING.name()));
        summary.setApprovedClaims(claimRepository.countByClaimStatus(ClaimStatus.APPROVED.name()));

        // Scheme-wise income
        List<Object[]> schemeData = paymentRepository.getSchemeWiseIncome();
        Map<String, Double> schemeWiseIncome = new LinkedHashMap<>();
        for (Object[] row : schemeData) {
            schemeWiseIncome.put((String) row[0], (Double) row[1]);
        }
        summary.setSchemeWiseIncome(schemeWiseIncome);

        // Monthly trends (last 12 months)
        summary.setMonthlyTrends(getMonthlyTrends());

        // Recent activity (last 10 payments)
        summary.setRecentActivity(getRecentActivity());

        return summary;
    }

    private List<MonthlyTrendDto> getMonthlyTrends() {
        PageRequest last12 = PageRequest.of(0, 12);
        List<Object[]> incomeData = paymentRepository.getMonthlyIncome(last12);
        List<Object[]> expenseData = claimRepository.getMonthlyClaimExpenses(last12);

        Map<String, Double> incomeMap = new LinkedHashMap<>();
        for (Object[] row : incomeData) {
            incomeMap.put((String) row[0], (Double) row[1]);
        }

        Map<String, Double> expenseMap = new LinkedHashMap<>();
        for (Object[] row : expenseData) {
            expenseMap.put((String) row[0], (Double) row[1]);
        }

        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(incomeMap.keySet());
        allMonths.addAll(expenseMap.keySet());

        return allMonths.stream()
                .map(month -> new MonthlyTrendDto(
                        month,
                        incomeMap.getOrDefault(month, 0.0),
                        expenseMap.getOrDefault(month, 0.0)))
                .collect(Collectors.toList());
    }

    private List<RecentActivityDto> getRecentActivity() {
        List<Payment> recentPayments = paymentRepository.findRecentPayments(PageRequest.of(0, 10));
        return recentPayments.stream()
                .map(p -> new RecentActivityDto(
                        "PAYMENT",
                        "Payment for policy " + p.getInsurancePolicy().getId(),
                        p.getAmount(),
                        p.getPaymentDate()))
                .collect(Collectors.toList());
    }
}
