package com.Diagnostic;


import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.entity.Appointment;
import com.Diagnostic.mapper.AppointmentMapper;
import com.Diagnostic.repository.AppointmentRepository;
import com.Diagnostic.service.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceImplTest {

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper mapper;

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

    @Test
    void testApplyForCheckup_ValidRequest_ShouldReturnConfirmedStatus() {
        AppointmentCheckupRequest request = getValidRequest();
        Appointment appointment = new Appointment();
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();

        when(mapper.toAppointment(request)).thenReturn(appointment);
        when(appointmentRepository.save(any())).thenReturn(appointment);
        when(mapper.toResponse(any())).thenReturn(response);

        AppointmentCheckupResponse result = appointmentService.applyForCheckup(request);

        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(appointment);
        assertEquals("Confirmed", appointment.getStatus());
    }

    @Test
    void testDeleteAppointmentById_ShouldDeleteSuccessfully() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId("test-id");

        when(appointmentRepository.findByAppointmentId("test-id")).thenReturn(Optional.of(appointment));
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        when(mapper.toResponse(appointment)).thenReturn(response);

        AppointmentCheckupResponse result = appointmentService.deleteAppointmentById("test-id");

        assertNotNull(result);
        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void testGetAppointmentDetailsById_ShouldReturnDetails() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId("test-id");

        when(appointmentRepository.findByAppointmentId("test-id")).thenReturn(Optional.of(appointment));
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        when(mapper.toResponse(appointment)).thenReturn(response);

        AppointmentCheckupResponse result = appointmentService.getAppointmentDetailsById("test-id");

        assertNotNull(result);
    }

    @Test
    void testUpdateAppointment_WithValidRequest_ShouldConfirmAppointment() {
        String appointmentId = "test-id";
        Appointment appointment = new Appointment();
        AppointmentCheckupRequest request = getValidRequest();
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();

        when(appointmentRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.of(appointment));
        doAnswer(invocation -> {
            AppointmentCheckupRequest req = invocation.getArgument(0);
            Appointment appt = invocation.getArgument(1);
            appt.setPatientName(req.getPatientName());
            return null;
        }).when(mapper).updateAppointmentFromRequest(eq(request), eq(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(mapper.toResponse(appointment)).thenReturn(response);

        AppointmentCheckupResponse result = appointmentService.updateAppointment(appointmentId, request);

        assertNotNull(result);
        assertEquals("Confirmed", appointment.getStatus());
    }
}

