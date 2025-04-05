package com.LeaveManagement.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import com.LeaveManagement.Repo.EmployeeRepo;
import com.LeaveManagement.Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import com.LeaveManagement.Model.Employee;
import com.LeaveManagement.Model.Gender;
import com.LeaveManagement.Model.Role;
import com.LeaveManagement.Service.EmployeeServiceImpl;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class EmployeeController{

	@Autowired
	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
	
	@Autowired
	EmployeeServiceImpl employeeServiceImpl;
    @Autowired
    private EmployeeRepo employeeRepo;

	@Autowired
	JwtService jwtService;

	private final LocalDate localDate = LocalDate.now();
	private final Date currentDate = java.sql.Date.valueOf(localDate);

	public int generateEmployeeId() {
		String lastEmplId = employeeRepo.findLastEmployeeId();

		int nextId = 1000; // Default start value
		if (lastEmplId != null) {
			// Extract the numeric part and increment it
			String numericPart = lastEmplId.replaceAll("[^0-9]", "");
			nextId = Integer.parseInt(numericPart) + 1;
		}

		// Return the new ID with a prefix
		return nextId;
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> Login(@RequestBody Employee employee) {
		Map<String, Object> response = new HashMap<>();

		Employee authEmployee =  employeeServiceImpl.verify(employee);
		System.out.println(authEmployee);
		if (authEmployee != null) {
			String token = jwtService.generateToken(authEmployee.getUsername());
			response.put("token", token);
			response.put("emplid", authEmployee.getEmplid());
			response.put("role", authEmployee.getRole());
			response.put("supervisorId", authEmployee.getSupervisorId());
			return ResponseEntity.ok(response);
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	}

	@PostMapping(value = "/admin/add")
	public ResponseEntity<String> addEmployee(@Valid @RequestBody Employee employeeRequest) {

		int emplid = generateEmployeeId();

		try {
			// Parse date of birth
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date dob = employeeRequest.getDateOfBirth();

			// Validate role
			Role role;
			try {
				role = employeeRequest.getRole();
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body("Invalid role provided.");
			}

			// Validate gender
			Gender gender;
			try {
				gender = employeeRequest.getGender();
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body("Invalid gender provided.");
			}

			// Create employee object
			Employee emp = new Employee();
			emp.setEmplid(emplid);
			emp.setName(employeeRequest.getName());
			emp.setEmail(employeeRequest.getEmail());
			emp.setSupervisorId(employeeRequest.getSupervisorId());
			emp.setRole(role);
			emp.setGender(gender);
			emp.setDateOfBirth(dob);
			emp.setLeaveBalance(20);
			emp.setUsername(employeeRequest.getName()+"@softcorp.com");
			emp.setPassword(passwordEncoder.encode(employeeRequest.getPassword()));
			emp.setCreatedAt(currentDate);
			emp.setUpdatedAt(currentDate);

			if(employeeServiceImpl.findEmployeeById(emp.getSupervisorId()) == null) {
				emp.setCreatedBy(employeeRequest.getName());
				emp.setUpdatedBy(employeeRequest.getName());
			}
			else
			{
				emp.setCreatedBy(employeeServiceImpl.findSupervisorNameByEmplid(employeeRequest.getSupervisorId()));
				emp.setUpdatedBy(employeeServiceImpl.findSupervisorNameByEmplid(employeeRequest.getSupervisorId()));
			}

			// Delegate setting leave balance and username to the service layer
			if(employeeRepo.findEmployeeByEmplid(employeeRequest.getEmplid()) == null) {
				employeeServiceImpl.addEmployee(emp);
				return ResponseEntity.ok("Employee added successfully.");
			}
			else {
				return ResponseEntity.badRequest().body("Employee already exists.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the employee.");
		}
	}

	@GetMapping("/csrf")
	public CsrfToken getCsrfToken(HttpServletRequest request) {
		return (CsrfToken) request.getAttribute("_csrf");
	}


	@GetMapping("/findAll")
	public ResponseEntity<List<Employee>> findAll() {
		List<Employee> employees = employeeServiceImpl.findAllEmployee();
		if (employees.isEmpty()) {
			return ResponseEntity.noContent().build(); // 204 No Content if the list is empty
		}
		return ResponseEntity.ok(employees); // 200 OK with the list of employees
	}

	// Fetch employee by ID
	@GetMapping("findByEmplid/{emplid}")
	public ResponseEntity<Employee> findByEmplid(@Valid @PathVariable int emplid) {
		Employee employee = employeeServiceImpl.findEmployeeById(emplid);
		if (employee == null) {
			return ResponseEntity.notFound().build(); // 404 Not Found if the employee doesn't exist
		}
		return ResponseEntity.ok(employee); // 200 OK with the employee details
	}

	public int LeaveBalance(int emplid) {

		return employeeServiceImpl.leaveBalance(emplid);
	}


}