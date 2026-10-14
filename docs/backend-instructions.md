# Context
You are an expert Java Spring Boot developer. I am building a backend for a Salary Management application for an HR persona. The system must manage 10,000 employee records efficiently. 

Please generate the necessary Spring Boot code (Controllers, Services, Repositories, Entities, and DTOs) based on the specifications below. I am using Java 17+ and Spring Boot 3+.

# 1. Dependencies Setup
Please provide the Maven `pom.xml` dependencies for:
- Spring Boot Web
- Spring Data JPA
- SQLite JDBC driver (`org.xerial:sqlite-jdbc`)
- Hibernate community dialects (if required for SQLite in this Spring Boot version)
- Lombok
- Datafaker (`net.datafaker:datafaker` for seeding)
- Spring Boot Starter Test (JUnit 5, Mockito)

Also, provide the `application.properties` configuration to connect to a local `salary-db.sqlite` file and automatically generate the schema (`spring.jpa.hibernate.ddl-auto=update`).

# 2. Database Schema & Entity
Create an `Employee` JPA entity with the following fields. Use Lombok annotations to reduce boilerplate.
- `id` (UUID or Long auto-increment)
- `firstName` (String)
- `lastName` (String)
- `jobTitle` (String)
- `department` (String) - *Add a database index for performance*
- `country` (String) - *Add a database index for performance*
- `salary` (BigDecimal)
- `currency` (String, default "USD")

# 3. Data Repository
Create an `EmployeeRepository` extending `JpaRepository`.
Add derived query methods to:
- Find employees by department and country with `Pageable` support.
- Calculate total sum of salaries.
- Calculate average salary by department.
- Calculate average salary by country.
*(Note: You may use `@Query` annotations with custom JPQL/native queries for the aggregations if derived methods are insufficient).*

# 4. Data Seeder (Command Line Runner)
Create a `DatabaseSeeder` component implementing `CommandLineRunner`.
- On application startup, check if the `EmployeeRepository` is empty.
- If empty, use `Datafaker` to generate exactly 10,000 realistic employee records.
- Spread the data across 5-6 realistic departments (e.g., Engineering, Sales, HR) and 4-5 countries (e.g., USA, India, UK, Germany) to make the analytics meaningful.
- Save the records efficiently in batches to ensure the application starts up within a few seconds.

# 5. Business Logic (Service Layer)
Create an `EmployeeService` that handles:
- Fetching a paginated list of employees (with optional filtering by department/country).
- Fetching a single employee by ID.
- Creating a new employee.
- Updating an existing employee's salary/details.
- Generating a `DashboardAnalyticsDTO` containing total payroll, headcount, and average salaries by department/country.

# 6. REST API (Controller Layer)
Create an `EmployeeController` and `AnalyticsController` exposing RESTful endpoints for the service layer methods. Ensure appropriate HTTP status codes (200, 201, 404) and use `@CrossOrigin` or standard CORS configuration so the Angular frontend can access it.

# 7. Unit Tests
Provide a suite of JUnit 5 and Mockito tests for the `EmployeeService` to verify that the business logic and pagination work correctly. The tests must be fast and deterministic.