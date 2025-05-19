package com.Diagnostic.service;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.entity.Appointment;
import com.Diagnostic.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.Diagnostic.utility.ValidationUtil.isNullOrEmpty;

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

        boolean isDateBeyond15Days = request.getPreferredDate().isAfter(LocalDate.now().plusDays(15));

        if (isDateBeyond15Days) {
            appointment.setStatus("Pending");
            appointment.setRemark("Choose date within 15 days");
        } else {
            appointment.setStatus("Confirmed");
            appointment.setRemark(null);
        }

        appointmentRepository.save(appointment);
        return toResponse(appointment);
    }

    @Transactional
    @Override
    public AppointmentCheckupResponse deleteAppointmentById(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus("Cancelled");

        appointmentRepository.delete(appointment);
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

        boolean hasMissingFields = validateMissingFields(request);
        if (!hasMissingFields) {
            appointment.setStatus("Confirmed");
            appointment.setRemark(null);
        } else {
            appointment.setStatus("In Progress");
            appointment.setRemark(getMissingFieldMessage(request));
        }

        appointmentRepository.save(appointment);
        return toResponse(appointment);
    }

    @Override
    public List<AppointmentCheckupResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Convert entity to response DTO manually
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

    private Appointment getAppointmentById(String appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));
    }

    private boolean validateMissingFields(AppointmentCheckupRequest request) {
        return isNullOrEmpty(request.getPatientName()) ||
                request.getAge() <= 0 ||
                isNullOrEmpty(request.getGender()) ||
                isNullOrEmpty(request.getMobile()) ||
                isNullOrEmpty(request.getEmail()) ||
                isNullOrEmpty(request.getCheckupType()) ||
                request.getPreferredDate() == null ||
                request.getPreferredTime() == null;
    }

    private String getMissingFieldMessage(AppointmentCheckupRequest request) {
        StringBuilder sb = new StringBuilder("Missing fields: ");
        if (isNullOrEmpty(request.getPatientName())) sb.append("Patient Name, ");
        if (request.getAge() <= 0) sb.append("Age, ");
        if (isNullOrEmpty(request.getGender())) sb.append("Gender, ");
        if (isNullOrEmpty(request.getMobile())) sb.append("Mobile, ");
        if (isNullOrEmpty(request.getEmail())) sb.append("Email, ");
        if (isNullOrEmpty(request.getCheckupType())) sb.append("Checkup Type, ");
        if (request.getPreferredDate() == null) sb.append("Preferred Date, ");
        if (request.getPreferredTime() == null) sb.append("Preferred Time, ");
        return sb.substring(0, sb.length() - 2); // Remove trailing comma and space
    }
}
