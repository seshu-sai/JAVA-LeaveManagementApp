package com.LeaveManagement.Service;

import java.util.List;

import com.LeaveManagement.Model.LeaveRequest;
import com.LeaveManagement.Model.LeaveStatus;

public interface LeaveRequestService{
	public List<LeaveRequest> findAll();
	public String applyLeave(LeaveRequest leaveRequest);
//	public String updateLeave(LeaveRequest leaveRequest);
	public void cancelLeave(int id);
	public LeaveRequest findLeaveById(int id);
	public List<LeaveRequest> findLeaveByLeaveStatus(LeaveStatus ls);
	public void approveLeaveManager(int id);
	public void rejectLeaveManager(int id);
	public List<LeaveRequest> findLeaveByEmplid(int emplid);
	public List<LeaveRequest> findLeaveBySupervisor(int supervisorId);
}