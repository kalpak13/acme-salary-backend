package com.acme.salarymanagement.dto;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardAnalyticsDTO(
        BigDecimal totalPayroll,
        long headcount,
        Map<String, BigDecimal> averageSalariesByDepartment,
        Map<String, BigDecimal> averageSalariesByCountry) {
}