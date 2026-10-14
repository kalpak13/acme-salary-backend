package com.acme.salarymanagement.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employees_department", columnList = "department"),
        @Index(name = "idx_employees_country", columnList = "country")
})
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String jobTitle;

    private String department;

    private String country;

    @Column(precision = 19, scale = 2)
    private BigDecimal salary;

    @Column(length = 3)
    private String currency = "USD";
}