package com.Diagnostic;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.entity.Appointment;
import com.Diagnostic.exception.AppointmentNotFoundException;
import com.Diagnostic.repository.AppointmentRepository;
import com.Diagnostic.service.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceImplTest {

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private AppointmentCheckupRequest getValidRequest() {
        AppointmentCheckupRequest request = new AppointmentCheckupRequest();
        request.setPatientName("John Doe");
        request.setAge(30);
        request.setGender("Male");
        request.setMobile("1234567890");
        request.setEmail("john@example.com");
        request.setCheckupType("General");
        request.setPreferredDate(LocalDate.now().plusDays(5));
        request.setPreferredTime(LocalTime.of(10, 30));
        return request;
    }

    private Appointment mapToAppointment(AppointmentCheckupRequest request) {
        Appointment appointment = new Appointment();
        appointment.setPatientName(request.getPatientName());
        appointment.setAge(request.getAge());
        appointment.setGender(request.getGender());
        appointment.setMobile(request.getMobile());
        appointment.setEmail(request.getEmail());
        appointment.setCheckupType(request.getCheckupType());
        appointment.setAppointmentDate(request.getPreferredDate());
        appointment.setAppointmentTime(request.getPreferredTime());
        appointment.setStatus("Confirmed");
        return appointment;
    }

    @Test
    void testApplyForCheckup_ValidRequest_ShouldReturnConfirmedStatus() {
        AppointmentCheckupRequest request = getValidRequest();

        when(appointmentRepository.save(any())).thenAnswer(invocation -> {
            Appointment savedAppointment = invocation.getArgument(0);
            savedAppointment.setAppointmentId("generated-id-123");
            return savedAppointment;
        });

        AppointmentCheckupResponse result = appointmentService.applyForCheckup(request);

        assertNotNull(result);
        assertEquals("Confirmed", result.getStatus());
        assertEquals("generated-id-123", result.getAppointmentId());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testApplyForCheckup_InvalidDate_ShouldReturnPendingStatus() {
        AppointmentCheckupRequest request = getValidRequest();
        request.setPreferredDate(LocalDate.now().plusDays(20)); // More than 15 days

        when(appointmentRepository.save(any())).thenAnswer(invocation -> {
            Appointment savedAppointment = invocation.getArgument(0);
            savedAppointment.setAppointmentId("generated-id-456");
            return savedAppointment;
        });

        AppointmentCheckupResponse result = appointmentService.applyForCheckup(request);

        assertNotNull(result);
        assertEquals("Pending", result.getStatus());
        assertEquals("Choose date within 15 days", result.getRemark());
    }

    @Test
    void testDeleteAppointmentById_ShouldDeleteSuccessfully() {
        Appointment appointment = mapToAppointment(getValidRequest());
        appointment.setAppointmentId("test-id");

        when(appointmentRepository.findByAppointmentId("test-id")).thenReturn(Optional.of(appointment));
        doNothing().when(appointmentRepository).delete(appointment);

        AppointmentCheckupResponse result = appointmentService.deleteAppointmentById("test-id");

        assertNotNull(result);
        assertEquals("Cancelled", result.getStatus());
        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void testDeleteAppointmentById_NotFound_ShouldThrowException() {
        when(appointmentRepository.findByAppointmentId("invalid-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.deleteAppointmentById("invalid-id");
        });

        assertEquals("Appointment not found with ID: invalid-id", exception.getMessage());
    }

    @Test
    void testGetAppointmentDetailsById_ShouldReturnDetails() {
        Appointment appointment = mapToAppointment(getValidRequest());
        appointment.setAppointmentId("test-id");

        when(appointmentRepository.findByAppointmentId("test-id")).thenReturn(Optional.of(appointment));

        AppointmentCheckupResponse result = appointmentService.getAppointmentDetailsById("test-id");

        assertNotNull(result);
        assertEquals("test-id", result.getAppointmentId());
    }

    @Test
    void testUpdateAppointment_WithValidRequest_ShouldConfirmAppointment() {
        String appointmentId = "test-id";
        Appointment appointment = mapToAppointment(getValidRequest());
        appointment.setAppointmentId(appointmentId);

        AppointmentCheckupRequest updatedRequest = getValidRequest();
        updatedRequest.setPatientName("Updated Name");

        when(appointmentRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentCheckupResponse result = appointmentService.updateAppointment(appointmentId, updatedRequest);

        assertNotNull(result);
        assertEquals("Confirmed", result.getStatus());
        assertEquals("Updated Name", result.getPatientName());
    }

    @Test
    void testGetAllAppointments_ShouldReturnList() {
        Appointment appointment1 = mapToAppointment(getValidRequest());
        appointment1.setAppointmentId("id1");

        Appointment appointment2 = mapToAppointment(getValidRequest());
        appointment2.setAppointmentId("id2");

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appointment1, appointment2));

        List<AppointmentCheckupResponse> result = appointmentService.getAllAppointments();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("id1", result.get(0).getAppointmentId());
        assertEquals("id2", result.get(1).getAppointmentId());
    }
}
