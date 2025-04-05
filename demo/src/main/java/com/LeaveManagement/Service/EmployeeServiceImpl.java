package com.LeaveManagement.Service;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.LeaveManagement.Model.Employee;
import com.LeaveManagement.Repo.EmployeeRepo;

@Service
public class EmployeeServiceImpl implements EmployeeService{
	
	@Autowired
	EmployeeRepo employeeRepo;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	@Override
	public Employee addEmployee(Employee emp) {
		return employeeRepo.save(emp);

	}

	@Override
	public List<Employee> findAllEmployee() {
		return employeeRepo.findAll();
	}

	@Override
	public Employee findEmployeeById(int emplid) {
		return employeeRepo.findEmployeeByEmplid(emplid);
	}

	@Override
	public int leaveBalance(int emplid) {
		System.out.println("leaveBalance "+employeeRepo.leaveBalanceByEmplid(emplid));
		return employeeRepo.leaveBalanceByEmplid(emplid);
	}

	@Transactional
	@Override
	public void updateLeaveBalance(int remainingLeaves, int emplid) {
		employeeRepo.updateLeaveBalance(remainingLeaves, emplid);
	}

	@Override
	public String findSupervisorNameByEmplid(int supervisorId) {
		return employeeRepo.findSupervisorNameByEmplid(supervisorId);
	}

	@Override
	public String findLastEmplid() {
		return employeeRepo.findLastEmployeeId();
	}


	public Employee verify(Employee employee) {
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(employee.getUsername(), employee.getPassword()));
		if(auth.isAuthenticated()) {
			return employeeRepo.findEmployeeByUsername(employee.getUsername());
		}
			else{
				return null;
			}
		}

}