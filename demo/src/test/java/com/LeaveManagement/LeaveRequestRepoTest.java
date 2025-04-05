package com.LeaveManagement;

import com.LeaveManagement.Model.LeaveRequest;
import com.LeaveManagement.Model.LeaveStatus;
import com.LeaveManagement.Repo.LeaveRequestRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LeaveRequestRepoTest {

    @Autowired
    private LeaveRequestRepo leaveRequestRepo;


        public Date toDate(LocalDate localDate) {
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

    @Test
    void shouldSaveAndFindLeaveRequest() {
        LeaveRequest request = new LeaveRequest();
        request.setEmplid(1000);
        request.setLeaveStartDate(toDate(LocalDate.now().plusDays(5)));
        request.setLeaveEndDate(toDate(LocalDate.now().plusDays(7)));
        request.setComment("Test Reason");
        request.setLeaveStatus(LeaveStatus.APPLIED);
        request.setSupervisorId(1001);
        request.setDescription("Test Description");

        leaveRequestRepo.save(request);

        List<LeaveRequest> found = leaveRequestRepo.findAll();
        assertFalse(found.isEmpty());
        assertEquals(1000, found.get(0).getEmplid());
        assertEquals("Test Reason", found.get(0).getComment());
        assertEquals("Test Description", found.get(0).getDescription());
    }





}
