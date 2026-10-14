package com.acme.salarymanagement.employee;

import com.acme.salarymanagement.dto.DashboardAnalyticsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Page<Employee> getEmployees(Pageable pageable, String department, String country) {
        boolean hasDepartment = StringUtils.hasText(department);
        boolean hasCountry = StringUtils.hasText(country);

        if (hasDepartment && hasCountry) {
            return employeeRepository.findByDepartmentAndCountry(department, country, pageable);
        }
        if (hasDepartment) {
            return employeeRepository.findByDepartment(department, pageable);
        }
        if (hasCountry) {
            return employeeRepository.findByCountry(country, pageable);
        }
        return employeeRepository.findAll(pageable);
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee createEmployee(Employee employee) {
        employee.setId(null);
        return employeeRepository.save(employee);
    }

    public Optional<Employee> updateEmployee(Long id, Employee updates) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setFirstName(updates.getFirstName());
            employee.setLastName(updates.getLastName());
            employee.setJobTitle(updates.getJobTitle());
            employee.setDepartment(updates.getDepartment());
            employee.setCountry(updates.getCountry());
            employee.setSalary(updates.getSalary());
            employee.setCurrency(updates.getCurrency());
            return employeeRepository.save(employee);
        });
    }

    public DashboardAnalyticsDTO getDashboardAnalytics() {
        return new DashboardAnalyticsDTO(
                employeeRepository.calculateTotalSalary(),
                employeeRepository.count(),
                toAverageMap(employeeRepository.calculateAverageSalaryByDepartment()),
                toAverageMap(employeeRepository.calculateAverageSalaryByCountry()));
    }

    private Map<String, BigDecimal> toAverageMap(List<Object[]> averages) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] average : averages) {
            Object value = average[1];
            BigDecimal salary = value == null ? null : toBigDecimal(value);
            result.put((String) average[0], salary);
        }
        return result;
    }

    private BigDecimal toBigDecimal(Object value) {
        BigDecimal average = value instanceof BigDecimal decimal
                ? decimal
                : BigDecimal.valueOf(((Number) value).doubleValue());
        return average.setScale(2, RoundingMode.HALF_UP);
    }
}