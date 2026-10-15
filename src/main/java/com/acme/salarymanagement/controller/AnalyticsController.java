package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.dto.DashboardAnalyticsDTO;
import com.acme.salarymanagement.employee.EmployeeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class AnalyticsController {

    private final EmployeeService employeeService;

    public AnalyticsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/dashboard")
    public DashboardAnalyticsDTO getDashboardAnalytics() {
        return employeeService.getDashboardAnalytics();
    }
}