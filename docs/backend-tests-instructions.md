# Context
You are an expert Java Spring Boot developer. I need a meaningful set of unit tests for the core functionality of my Salary Management application. The assessment requires these tests to be fast, deterministic, and easy to understand. 

Please generate unit tests using JUnit 5, Mockito, and AssertJ. Do NOT load the full Spring application context or connect to a real database.

# 1. EmployeeService Tests
Create `EmployeeServiceTest.java`. 
Mock the `EmployeeRepository` completely.
Write tests for the following scenarios:
- `getEmployees`: Verify that passing pagination and filter parameters correctly calls the repository and returns a paginated result.
- `getDashboardAnalytics`: Verify that the analytics DTO is correctly populated by mocking the repository aggregation methods.
- `addEmployee`: Verify that a new employee is saved successfully.
- `updateEmployee`: Verify that updating an existing employee's salary works, and test the failure case (e.g., throwing a `ResourceNotFoundException` if the ID does not exist).

# 2. EmployeeController Tests (Optional but recommended)
Create `EmployeeControllerTest.java` using `@WebMvcTest`.
- Use `MockMvc` to test the REST endpoints.
- Mock the `EmployeeService`.
- Verify that the GET, POST, and PUT endpoints return the correct HTTP status codes (200 OK, 201 Created, 404 Not Found) and JSON structures.