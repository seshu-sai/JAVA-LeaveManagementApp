package com.LeaveManagement.Repo;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LeaveManagement.Model.Employee;
import com.LeaveManagement.Model.LeaveRequest;
import com.LeaveManagement.Model.LeaveStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, Integer> {
	
	
	public List<LeaveRequest> findLeaveRequestByEmplid(int id);
	
	
	
	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.leaveStatus = :leaveStatus")
	List<LeaveRequest> findLeaveByLeaveStatus(@Param("leaveStatus") LeaveStatus ls);

	@Query("SELECT l FROM LeaveRequest l WHERE l.supervisorId = :supervisorId")
	List<LeaveRequest> findBySupervisorId(@Param("supervisorId") int supervisorId);


}
