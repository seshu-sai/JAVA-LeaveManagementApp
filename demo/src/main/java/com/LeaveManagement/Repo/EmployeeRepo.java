package com.LeaveManagement.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LeaveManagement.Model.Employee;


@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

	@Query("select ep from Employee ep where ep.emplid = :emplid" )
	public Employee findEmployeeByEmplid(@Param("emplid")int emplid);

	@Query("SELECT ep.leaveBalance FROM Employee ep WHERE ep.emplid = :emplid")
	public int leaveBalanceByEmplid(@Param("emplid") int emplid);

	@Modifying
	@Query("UPDATE Employee ep SET ep.leaveBalance = :remainingLeaves where ep.emplid = :emplid")
	public void updateLeaveBalance(@Param("remainingLeaves") int remainingLeaves, @Param("emplid")int emplid);

	@Query("SELECT u FROM Employee u WHERE u.username = :username")
	public Employee findEmployeeByUsername(@Param("username") String username);

	@Query("SELECT u.name FROM Employee u WHERE u.emplid = :supervisorId")
	public String findSupervisorNameByEmplid(@Param("supervisorId") int supervisorId);

	@Query("SELECT u.emplid FROM Employee u ORDER BY u.id DESC LIMIT 1")
	public String findLastEmployeeId();

	@Query("SELECT u FROM Employee u WHERE u.username = :username")
	public String findLastEmployeeId(@Param("username") String username);
}
