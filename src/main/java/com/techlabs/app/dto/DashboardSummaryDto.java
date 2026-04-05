package com.techlabs.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDto {
    private Double totalIncome;
    private Double totalExpenses;
    private Double netBalance;
    private Long totalPolicies;
    private Long activePolicies;
    private Long totalCustomers;
    private Long totalAgents;
    private Long pendingClaims;
    private Long approvedClaims;
    private Map<String, Double> schemeWiseIncome;
    private List<MonthlyTrendDto> monthlyTrends;
    private List<RecentActivityDto> recentActivity;
}
