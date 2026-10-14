package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.employee.Employee;
import com.acme.salarymanagement.employee.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void getEmployeesReturnsFilteredPageAndPassesPaginationToService() throws Exception {
        Employee employee = employee(1L, "Avery", "Morgan", "Engineering", "Canada", "125000.00");
        when(employeeService.getEmployees(any(Pageable.class), eq("Engineering"), eq("Canada")))
                .thenReturn(new PageImpl<>(List.of(employee), PageRequest.of(1, 2), 3));

        mockMvc.perform(get("/api/employees")
                        .param("page", "1")
                        .param("size", "2")
                        .param("department", "Engineering")
                        .param("country", "Canada"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Avery"))
                .andExpect(jsonPath("$.content[0].salary").value(125000.00))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeService).getEmployees(pageableCaptor.capture(), eq("Engineering"), eq("Canada"));
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(1, 2));
    }

    @Test
    void getEmployeeByIdReturnsEmployeeWhenFound() throws Exception {
        when(employeeService.getEmployeeById(7L))
                .thenReturn(Optional.of(employee(7L, "Avery", "Morgan", "Engineering", "Canada", "125000.00")));

        mockMvc.perform(get("/api/employees/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.firstName").value("Avery"))
                .andExpect(jsonPath("$.department").value("Engineering"));
    }

    @Test
    void getEmployeeByIdReturnsNotFoundWhenMissing() throws Exception {
        when(employeeService.getEmployeeById(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployeeReturnsCreatedAndSerializedEmployee() throws Exception {
        Employee employee = employee(null, "Jordan", "Lee", "Finance", "India", "95000.00");
        Employee savedEmployee = employee(15L, "Jordan", "Lee", "Finance", "India", "95000.00");
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(savedEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.firstName").value("Jordan"))
                .andExpect(jsonPath("$.salary").value(95000.00));
    }

    @Test
    void updateEmployeeReturnsUpdatedEmployeeWhenFound() throws Exception {
        Employee updates = employee(null, "Avery", "Morgan", "Engineering", "Canada", "130000.00");
        Employee savedEmployee = employee(7L, "Avery", "Morgan", "Engineering", "Canada", "130000.00");
        when(employeeService.updateEmployee(eq(7L), any(Employee.class))).thenReturn(Optional.of(savedEmployee));

        mockMvc.perform(put("/api/employees/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.salary").value(130000.00));
    }

    @Test
    void updateEmployeeReturnsNotFoundWhenMissing() throws Exception {
        when(employeeService.updateEmployee(eq(404L), any(Employee.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/employees/404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee(
                                null, "Avery", "Morgan", "Engineering", "Canada", "130000.00"))))
                .andExpect(status().isNotFound());
    }

    private Employee employee(
            Long id,
            String firstName,
            String lastName,
            String department,
            String country,
            String salary) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setJobTitle("Analyst");
        employee.setDepartment(department);
        employee.setCountry(country);
        employee.setSalary(new BigDecimal(salary));
        employee.setCurrency("USD");
        return employee;
    }
}