package com.LeaveManagement.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import com.LeaveManagement.Repo.EmployeeRepo;
import com.LeaveManagement.Service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.LeaveManagement.Model.Employee;
import com.LeaveManagement.Model.LeaveRequest;
import com.LeaveManagement.Model.LeaveStatus;
import com.LeaveManagement.Service.EmployeeServiceImpl;
import com.LeaveManagement.Service.LeaveRequestServiceImpl;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/leave")
public class LeaveRequestController {

	@Autowired
	private EmployeeServiceImpl employeeServiceImpl;

	@Autowired
	private LeaveRequestServiceImpl leaveRequestServiceImpl;

	@Autowired
	private EmployeeService employeeService;

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	private final LocalDate localDate = LocalDate.now();
	private final Date currentDate = java.sql.Date.valueOf(localDate);


	// Calculate leave duration
	private int calculateDuration(Date startDate, Date endDate) {
		long durationInMillis = endDate.getTime() - startDate.getTime();
		return (int) (durationInMillis / (24 * 60 * 60 * 1000));
	}

	// Apply Leave
	@PostMapping("/apply")
	public ResponseEntity<String> applyLeave( @RequestBody LeaveRequest leaveRequest) throws ParseException {

		Date startDate = leaveRequest.getLeaveStartDate();
		Date endDate = leaveRequest.getLeaveEndDate();

		// Validate dates
		if (endDate.before(currentDate) || startDate.before(currentDate)) {
			return ResponseEntity.badRequest().body("You cannot apply for leaves in the past.");
		}
		if (endDate.before(startDate)) {
			return ResponseEntity.badRequest().body("End date must be greater than start date.");
		}

		// Calculate leave duration
		int duration = calculateDuration(startDate, endDate);
		leaveRequest.setDuration(duration);

		// Check employee leave balance
		Employee employee = employeeServiceImpl.findEmployeeById(leaveRequest.getEmplid());
		if (employee == null) {
			return ResponseEntity.badRequest().body("Employee not found.");
		}
		int leaveBalance = employee.getLeaveBalance();
		int supervisorId = employee.getSupervisorId();
		leaveRequest.setCreatedBy(employee.getName());
		leaveRequest.setCreatedAt(currentDate);
		leaveRequest.setUpdatedBy(employee.getName());
		leaveRequest.setUpdatedAt(currentDate);


		if (leaveBalance < duration) {
			return ResponseEntity.badRequest().body("Insufficient leave balance.");
		}

		//leave overlap check
		List<LeaveRequest> existingLeaves = leaveRequestServiceImpl.findLeaveByEmplid(leaveRequest.getEmplid());

		for (LeaveRequest existingLeave : existingLeaves) {
			// Check if the new leave overlaps with any existing leave request
			if ((startDate.before(existingLeave.getLeaveEndDate()) && endDate.after(existingLeave.getLeaveStartDate()))) {
				return ResponseEntity.badRequest().body("Leave dates overlap with an existing leave request.");
			}
		}

		// Deduct leave balance and save the request
		int remainingLeaves = leaveBalance - duration;
		System.out.println("Remaining balance: " + remainingLeaves);
		employeeServiceImpl.updateLeaveBalance(remainingLeaves, leaveRequest.getEmplid());
		leaveRequest.setLeaveBalance(remainingLeaves);
		leaveRequest.setLeaveStatus(LeaveStatus.APPLIED);
		leaveRequest.setSupervisorId(supervisorId);
		leaveRequestServiceImpl.applyLeave(leaveRequest);

		return ResponseEntity.ok("Leave applied successfully.");
	}

	// Cancel Leave
	@DeleteMapping("/cancel/{id}")
	public ResponseEntity<String> cancelLeave(@PathVariable int id) {
		leaveRequestServiceImpl.cancelLeave(id);
		return ResponseEntity.ok("Leave cancelled successfully.");
	}

	// Find Leave by ID
	@GetMapping("/{id}")
	public ResponseEntity<LeaveRequest> findById(@PathVariable int id) {
		LeaveRequest leaveRequest = leaveRequestServiceImpl.findLeaveById(id);
		if (leaveRequest == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(leaveRequest);
	}

	// List Leaves by Employee ID
	@GetMapping("/listByEmployee/{emplid}")
	public ResponseEntity<List<LeaveRequest>> listLeaveByEmployee(@PathVariable int emplid) {
		List<LeaveRequest> leaveRequests = leaveRequestServiceImpl.findLeaveByEmplid(emplid);
		if (leaveRequests.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(leaveRequests);
	}

	// List Leaves by Status
	@GetMapping("/listByStatus/{status}")
	public ResponseEntity<List<LeaveRequest>> listLeaveByStatus(@PathVariable LeaveStatus status) {
		List<LeaveRequest> leaveRequests = leaveRequestServiceImpl.findLeaveByLeaveStatus(status.APPLIED);
		if (leaveRequests.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(leaveRequests);
	}


	@PutMapping("/approve/{id}")
	public ResponseEntity<String> approveLeave(@PathVariable int id) {
		leaveRequestServiceImpl.approveLeaveManager(id);
		return ResponseEntity.ok("Leave approved successfully.");
	}

	@PutMapping("/reject/{id}")
	public ResponseEntity<String> rejectLeave(@PathVariable int id) {
		leaveRequestServiceImpl.rejectLeaveManager(id);
		return ResponseEntity.ok("Leave rejected successfully.");
	}

	@GetMapping("/listBySupervisor/{supervisorId}")
	public ResponseEntity<List<LeaveRequest>> listLeaveBySupervisor(@PathVariable int supervisorId) {
		List<LeaveRequest> leaveRequests = leaveRequestServiceImpl.findLeaveBySupervisor(supervisorId);
		return ResponseEntity.ok(leaveRequests);
	}

}
