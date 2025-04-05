package com.LeaveManagement;

import com.LeaveManagement.Model.LeaveRequest;
import com.LeaveManagement.Model.LeaveStatus;
import com.LeaveManagement.Repo.LeaveRequestRepo;
import com.LeaveManagement.Service.LeaveRequestServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepo leaveRequestRepo;


    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    private LeaveRequest sampleRequest;

    public Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @BeforeEach
    void setUp() {
        sampleRequest = new LeaveRequest();
        sampleRequest.setEmplid(1000);
        sampleRequest.setLeaveStartDate(toDate(LocalDate.now().plusDays(5)));
        sampleRequest.setLeaveEndDate(toDate(LocalDate.now().plusDays(7)));
        sampleRequest.setComment("Test Reason");
        sampleRequest.setLeaveStatus(LeaveStatus.APPLIED);
        sampleRequest.setSupervisorId(1001);
        sampleRequest.setDescription("Test Description");
    }

    @Test
    void shouldReturnAllLeaveRequests() {
        when(leaveRequestRepo.findAll()).thenReturn(Arrays.asList(sampleRequest));
        List<LeaveRequest> result = leaveRequestService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void shouldApplyLeave() {
        when(leaveRequestRepo.save(sampleRequest)).thenReturn(sampleRequest);
        String result = leaveRequestService.applyLeave(sampleRequest);
        assertEquals("leave applied succesfully", result);
    }

    @Test
    void shouldFindLeaveById() {
        when(leaveRequestRepo.findById(1)).thenReturn(Optional.of(sampleRequest));
        LeaveRequest found = leaveRequestService.findLeaveById(1);
        assertEquals(1000, found.getEmplid());
    }

    @Test
    void shouldFindLeaveByLeaveStatus() {
        when(leaveRequestRepo.findLeaveByLeaveStatus(LeaveStatus.APPLIED)).thenReturn(Arrays.asList(sampleRequest));
        List<LeaveRequest> result = leaveRequestService.findLeaveByLeaveStatus(LeaveStatus.APPLIED);
        assertEquals(1, result.size());
    }

    @Test
    void shouldFindLeaveByEmployeeId() {
        when(leaveRequestRepo.findLeaveRequestByEmplid(123)).thenReturn(Arrays.asList(sampleRequest));
        List<LeaveRequest> result = leaveRequestService.findLeaveByEmplid(123);
        assertEquals(1, result.size());
    }

    @Test
    void shouldFindLeaveBySupervisorId() {
        when(leaveRequestRepo.findBySupervisorId(1001)).thenReturn(Arrays.asList(sampleRequest));
        List<LeaveRequest> result = leaveRequestService.findLeaveBySupervisor(1001);
        assertEquals(1, result.size());
    }


}
