package com.Diagnostic.service;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.entity.Appointment;
import com.Diagnostic.exception.AppointmentNotFoundException;
import com.Diagnostic.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public AppointmentCheckupResponse applyForCheckup(AppointmentCheckupRequest request) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID().toString());
        appointment.setPatientName(request.getPatientName());
        appointment.setAge(request.getAge());
        appointment.setGender(request.getGender());
        appointment.setMobile(request.getMobile());
        appointment.setEmail(request.getEmail());
        appointment.setCheckupType(request.getCheckupType());
        appointment.setAppointmentDate(request.getPreferredDate());
        appointment.setAppointmentTime(request.getPreferredTime());

        // 15-day logic
        if (request.getPreferredDate().isAfter(LocalDate.now().plusDays(15))) {
            appointment.setStatus("Pending");
            appointment.setRemark("Choose date within 15 days");
        } else {
            appointment.setStatus("Confirmed");
            appointment.setRemark(null);
        }

        appointmentRepository.save(appointment);
        return toResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse deleteAppointmentById(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus("Cancelled");
        appointmentRepository.delete(appointment); // optional, or soft-delete by updating only
        return toResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse getAppointmentDetailsById(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        return toResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse updateAppointment(String appointmentId, AppointmentCheckupRequest request) {
        Appointment appointment = getAppointmentById(appointmentId);

        appointment.setPatientName(request.getPatientName());
        appointment.setAge(request.getAge());
        appointment.setGender(request.getGender());
        appointment.setMobile(request.getMobile());
        appointment.setEmail(request.getEmail());
        appointment.setCheckupType(request.getCheckupType());
        appointment.setAppointmentDate(request.getPreferredDate());
        appointment.setAppointmentTime(request.getPreferredTime());

        // Updating status and remark
        if (request.getPreferredDate().isAfter(LocalDate.now().plusDays(15))) {
            appointment.setStatus("Pending");
            appointment.setRemark("Choose date within 15 days");
        } else {
            appointment.setStatus("Confirmed");
            appointment.setRemark(null);
        }

        appointmentRepository.save(appointment);
        return toResponse(appointment);
    }

    @Override
    public List<AppointmentCheckupResponse> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Convert Entity → DTO
    private AppointmentCheckupResponse toResponse(Appointment appointment) {
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId(appointment.getAppointmentId());
        response.setPatientName(appointment.getPatientName());
        response.setCheckupType(appointment.getCheckupType());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());
        response.setRemark(appointment.getRemark());
        return response;
    }

    // Fetch by ID with custom exception
    private Appointment getAppointmentById(String appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));
    }
}
