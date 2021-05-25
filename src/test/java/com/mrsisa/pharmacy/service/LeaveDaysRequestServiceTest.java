package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.domain.entities.PharmacyEmployee;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.LeaveDaysRequestStatus;
import com.mrsisa.pharmacy.repository.ILeaveDaysRequestRepository;
import com.mrsisa.pharmacy.repository.IPharmacyEmployeeRepository;
import com.mrsisa.pharmacy.service.impl.AppointmentService;
import com.mrsisa.pharmacy.service.impl.LeaveDaysRequestService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LeaveDaysRequestServiceTest {

    @Mock
    private IPharmacyEmployeeRepository pharmacyEmployeeRepositoryMock;

    @Mock
    private ILeaveDaysRequestRepository leaveDaysRequestRepositoryMock;

    @InjectMocks
    private LeaveDaysRequestService leaveDaysRequestService;

    @Test
    @Transactional
    void testCreateLeaveDaysRequest() {
        // Test case constants
        final Long EMPLOYEE_ID = 123L;
        final LocalDate FROM_DATE = LocalDate.of(2021, 5, 15);
        final LocalDate TO_DATE = LocalDate.of(2021, 5, 25);

        // Create dummy data
        PharmacyEmployee pharmacyEmployee = new PharmacyEmployee();
        pharmacyEmployee.setId(EMPLOYEE_ID);

        // Mock repositories
        when(pharmacyEmployeeRepositoryMock.findById(eq(EMPLOYEE_ID))).thenReturn(Optional.of(pharmacyEmployee));

        // Verification
        LeaveDaysRequest leaveDaysRequest = leaveDaysRequestService.createLeaveDaysRequest(EMPLOYEE_ID, FROM_DATE, TO_DATE);
        assertEquals(leaveDaysRequest.getLeaveDaysRequestStatus(), LeaveDaysRequestStatus.PENDING);
        assertEquals(leaveDaysRequest.getEmployee(), pharmacyEmployee);
        assertTrue(leaveDaysRequest.getFrom().isEqual(FROM_DATE));
        assertTrue(leaveDaysRequest.getTo().isEqual(TO_DATE));
        verify(pharmacyEmployeeRepositoryMock, times(1)).findById(eq(EMPLOYEE_ID));
        verify(leaveDaysRequestRepositoryMock, times(1)).save(leaveDaysRequest);
    }
}
