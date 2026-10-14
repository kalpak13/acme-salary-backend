package com.acme.salarymanagement.seed;

import com.acme.salarymanagement.employee.EmployeeRepository;
import net.datafaker.Faker;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final int EMPLOYEE_COUNT = 10_000;
    private static final int BATCH_SIZE = 500;
    private static final String INSERT_EMPLOYEE_SQL = """
            insert into employees (first_name, last_name, job_title, department, country, salary, currency)
            values (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final List<String> DEPARTMENTS = List.of(
            "Engineering", "Sales", "Human Resources", "Finance", "Marketing", "Operations");
    private static final List<String> COUNTRIES = List.of(
            "United States", "India", "United Kingdom", "Germany", "Canada");

    private final EmployeeRepository employeeRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Faker faker = new Faker();

    public DatabaseSeeder(EmployeeRepository employeeRepository, JdbcTemplate jdbcTemplate) {
        this.employeeRepository = employeeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (employeeRepository.count() > 0) {
            return;
        }

        for (int offset = 0; offset < EMPLOYEE_COUNT; offset += BATCH_SIZE) {
            List<SeedEmployee> batch = createBatch(offset, Math.min(BATCH_SIZE, EMPLOYEE_COUNT - offset));
            jdbcTemplate.batchUpdate(INSERT_EMPLOYEE_SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement statement, int index) throws SQLException {
                    SeedEmployee employee = batch.get(index);
                    statement.setString(1, employee.firstName());
                    statement.setString(2, employee.lastName());
                    statement.setString(3, employee.jobTitle());
                    statement.setString(4, employee.department());
                    statement.setString(5, employee.country());
                    statement.setBigDecimal(6, employee.salary());
                    statement.setString(7, "USD");
                }

                @Override
                public int getBatchSize() {
                    return batch.size();
                }
            });
        }
    }

    private List<SeedEmployee> createBatch(int offset, int size) {
        List<SeedEmployee> batch = new ArrayList<>(size);
        for (int index = offset; index < offset + size; index++) {
            long salaryInCents = faker.number().numberBetween(4_000_000L, 20_000_001L);
            batch.add(new SeedEmployee(
                    faker.name().firstName(),
                    faker.name().lastName(),
                    faker.job().title(),
                    DEPARTMENTS.get(index % DEPARTMENTS.size()),
                    COUNTRIES.get(index % COUNTRIES.size()),
                    BigDecimal.valueOf(salaryInCents, 2)));
        }
        return batch;
    }

    private record SeedEmployee(
            String firstName,
            String lastName,
            String jobTitle,
            String department,
            String country,
            BigDecimal salary) {
    }
}