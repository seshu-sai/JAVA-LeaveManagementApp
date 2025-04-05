package com.LeaveManagement;

import com.LeaveManagement.Model.Employee;
import com.LeaveManagement.Repo.EmployeeRepo;
import com.LeaveManagement.Service.EmployeeServiceImpl;
import com.LeaveManagement.Service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private AuthenticationManager authenticationManager;


    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmplid(1000);
        employee.setName("Sheshu");
        employee.setPassword("Sheshu@0902");
        employee.setEmail("test@test.com");
    }

    @Test
    void shouldAddEmployee() {
        when(employeeRepo.save(employee)).thenReturn(employee);
        Employee saved = employeeService.addEmployee(employee);
        assertNotNull(saved);
        assertEquals(1000, saved.getEmplid());
    }

    @Test
    void shouldFindAllEmployees() {
        when(employeeRepo.findAll()).thenReturn(Arrays.asList(employee));
        List<Employee> result = employeeService.findAllEmployee();
        assertEquals(1, result.size());
    }

    @Test
    void shouldFindEmployeeById() {
        when(employeeRepo.findEmployeeByEmplid(123)).thenReturn(employee);
        Employee result = employeeService.findEmployeeById(123);
        assertEquals("Sheshu", result.getName());
    }

    @Test
    void shouldReturnLeaveBalance() {
        when(employeeRepo.leaveBalanceByEmplid(123)).thenReturn(10);
        int balance = employeeService.leaveBalance(123);
        assertEquals(10, balance);
    }

    @Test
    void shouldUpdateLeaveBalance() {
        employeeService.updateLeaveBalance(8, 123);
        verify(employeeRepo).updateLeaveBalance(8, 123);
    }

    @Test
    void shouldFindSupervisorName() {
        when(employeeRepo.findSupervisorNameByEmplid(500)).thenReturn("Jane Doe");
        String supervisorName = employeeService.findSupervisorNameByEmplid(500);
        assertEquals("Jane Doe", supervisorName);
    }

    @Test
    void shouldFindLastEmployeeId() {
        when(employeeRepo.findLastEmployeeId()).thenReturn("EMP123");
        String lastId = employeeService.findLastEmplid();
        assertEquals("EMP123", lastId);
    }

}
