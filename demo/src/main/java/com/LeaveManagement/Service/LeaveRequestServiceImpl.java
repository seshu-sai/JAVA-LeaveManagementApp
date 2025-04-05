package com.LeaveManagement.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.LeaveManagement.Model.LeaveRequest;
import com.LeaveManagement.Model.LeaveStatus;
import com.LeaveManagement.Repo.LeaveRequestRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService{
	
	@Autowired
	LeaveRequestRepo leaveRequestRepo;
	
	 @PersistenceContext
	  EntityManager entityManager;

	@Override
	public List<LeaveRequest> findAll() {
	 List<LeaveRequest> list = leaveRequestRepo.findAll();
	 return list;
	}

	@Override
	public String applyLeave(LeaveRequest leaveRequest) {
		leaveRequestRepo.save(leaveRequest);
		return "leave applied succesfully";
	}
//
//	@Override
//	public String updateLeave(LeaveRequest leaveRequest) {
//		leaveRequestRepo.save(leaveRequest);
//		return "leave updated succesfully";
//	}

	@Override
	@Transactional
	public void cancelLeave(int id) {
		        entityManager.createQuery("delete from LeaveRequest lr where lr.id = :id")
		                     .setParameter("id", id)
		                     .executeUpdate();
		    
	}

	@Override
	public LeaveRequest findLeaveById(int id) {
	return  leaveRequestRepo.findById(id).get();
		
	}

	@Override
	public List<LeaveRequest> findLeaveByLeaveStatus(LeaveStatus ls) {
		List<LeaveRequest> list = leaveRequestRepo.findLeaveByLeaveStatus(ls.APPLIED);
		return list;
	}

	@Override
	@Transactional
	public void approveLeaveManager(int id) {
		entityManager.createQuery("update LeaveRequest lr set lr.leaveStatus='APPROVED' where lr.id = :id")
        .setParameter("id", id)
        .executeUpdate();
	}


	@Override
	@Transactional
	public void rejectLeaveManager(int id) {
		entityManager.createQuery("update LeaveRequest lr set lr.leaveStatus='REJECTED' where lr.id = :id")
        .setParameter("id", id)
        .executeUpdate();
	}

	@Override
	public List<LeaveRequest> findLeaveByEmplid(int emplid) {
	return leaveRequestRepo.findLeaveRequestByEmplid(emplid);
	
	}

	public List<LeaveRequest> findLeaveBySupervisor(int supervisorId) {
		return leaveRequestRepo.findBySupervisorId(supervisorId);
	}


}