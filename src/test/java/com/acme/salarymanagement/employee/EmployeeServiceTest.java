package com.acme.salarymanagement.employee;

import com.acme.salarymanagement.dto.DashboardAnalyticsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private final Pageable pageable = PageRequest.of(1, 20);

    @Test
    void getEmployeesWithoutFiltersReturnsUnfilteredPage() {
        Page<Employee> expected = new PageImpl<>(List.of(new Employee()), pageable, 21);
        when(employeeRepository.findAll(pageable)).thenReturn(expected);

        Page<Employee> actual = employeeService.getEmployees(pageable, null, " ");

        assertThat(actual).isSameAs(expected);
        verify(employeeRepository).findAll(pageable);
    }

    @Test
    void getEmployeesByDepartmentReturnsFilteredPage() {
        Page<Employee> expected = Page.empty(pageable);
        when(employeeRepository.findByDepartment("Engineering", pageable)).thenReturn(expected);

        Page<Employee> actual = employeeService.getEmployees(pageable, "Engineering", null);

        assertThat(actual).isSameAs(expected);
        verify(employeeRepository).findByDepartment("Engineering", pageable);
    }

    @Test
    void getEmployeesByCountryReturnsFilteredPage() {
        Page<Employee> expected = Page.empty(pageable);
        when(employeeRepository.findByCountry("India", pageable)).thenReturn(expected);

        Page<Employee> actual = employeeService.getEmployees(pageable, null, "India");

        assertThat(actual).isSameAs(expected);
        verify(employeeRepository).findByCountry("India", pageable);
    }

    @Test
    void getEmployeesByDepartmentAndCountryReturnsFilteredPage() {
        Page<Employee> expected = Page.empty(pageable);
        when(employeeRepository.findByDepartmentAndCountry("Engineering", "India", pageable))
                .thenReturn(expected);

        Page<Employee> actual = employeeService.getEmployees(pageable, "Engineering", "India");

        assertThat(actual).isSameAs(expected);
        verify(employeeRepository).findByDepartmentAndCountry("Engineering", "India", pageable);
    }

    @Test
    void getEmployeeByIdReturnsRepositoryResult() {
        Employee employee = new Employee();
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        Optional<Employee> actual = employeeService.getEmployeeById(7L);

        assertThat(actual).containsSame(employee);
        verify(employeeRepository).findById(7L);
    }

    @Test
    void createEmployeeClearsClientProvidedIdBeforeSaving() {
        Employee employee = new Employee();
        employee.setId(999L);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee created = employeeService.createEmployee(employee);

        assertThat(created.getId()).isNull();
        verify(employeeRepository).save(employee);
    }

    @Test
    void updateEmployeeCopiesDetailsWithoutChangingExistingId() {
        Employee existing = new Employee();
        existing.setId(7L);
        Employee updates = new Employee();
        updates.setId(999L);
        updates.setFirstName("Avery");
        updates.setLastName("Morgan");
        updates.setJobTitle("Senior Engineer");
        updates.setDepartment("Engineering");
        updates.setCountry("Canada");
        updates.setSalary(new BigDecimal("125000.00"));
        updates.setCurrency("CAD");
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(existing)).thenReturn(existing);

        Optional<Employee> result = employeeService.updateEmployee(7L, updates);

        assertThat(result).containsSame(existing);
        assertThat(existing.getId()).isEqualTo(7L);
        assertThat(existing.getFirstName()).isEqualTo("Avery");
        assertThat(existing.getLastName()).isEqualTo("Morgan");
        assertThat(existing.getJobTitle()).isEqualTo("Senior Engineer");
        assertThat(existing.getDepartment()).isEqualTo("Engineering");
        assertThat(existing.getCountry()).isEqualTo("Canada");
        assertThat(existing.getSalary()).isEqualByComparingTo("125000.00");
        assertThat(existing.getCurrency()).isEqualTo("CAD");
        verify(employeeRepository).save(existing);
    }

    @Test
    void updateEmployeeReturnsEmptyAndDoesNotSaveWhenMissing() {
        when(employeeRepository.findById(404L)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.updateEmployee(404L, new Employee());

        assertThat(result).isEmpty();
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getDashboardAnalyticsCombinesTotalsHeadcountAndRoundedAverages() {
        when(employeeRepository.calculateTotalSalary()).thenReturn(new BigDecimal("250000.00"));
        when(employeeRepository.count()).thenReturn(2L);
        when(employeeRepository.calculateAverageSalaryByDepartment())
                .thenReturn(List.<Object[]>of(new Object[]{"Engineering", 125000.125}));
        when(employeeRepository.calculateAverageSalaryByCountry())
                .thenReturn(List.<Object[]>of(new Object[]{"Canada", new BigDecimal("125000.125")}));

        DashboardAnalyticsDTO analytics = employeeService.getDashboardAnalytics();

        assertThat(analytics.totalPayroll()).isEqualByComparingTo("250000.00");
        assertThat(analytics.headcount()).isEqualTo(2L);
        assertThat(analytics.averageSalariesByDepartment())
                .containsExactlyEntriesOf(Map.of("Engineering", new BigDecimal("125000.13")));
        assertThat(analytics.averageSalariesByCountry())
                .containsExactlyEntriesOf(Map.of("Canada", new BigDecimal("125000.13")));
        verify(employeeRepository).calculateTotalSalary();
        verify(employeeRepository).count();
        verify(employeeRepository).calculateAverageSalaryByDepartment();
        verify(employeeRepository).calculateAverageSalaryByCountry();
    }
}