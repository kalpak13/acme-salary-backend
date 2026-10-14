package com.acme.salarymanagement.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

        Page<Employee> findByDepartment(String department, Pageable pageable);

        Page<Employee> findByCountry(String country, Pageable pageable);

    Page<Employee> findByDepartmentAndCountry(String department, String country, Pageable pageable);

    @Query("select coalesce(sum(employee.salary), 0) from Employee employee")
    BigDecimal calculateTotalSalary();

    @Query("select employee.department, avg(employee.salary) "
            + "from Employee employee group by employee.department")
    List<Object[]> calculateAverageSalaryByDepartment();

    @Query("select employee.country, avg(employee.salary) "
            + "from Employee employee group by employee.country")
    List<Object[]> calculateAverageSalaryByCountry();
}