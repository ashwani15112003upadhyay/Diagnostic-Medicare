package com.Diagnostic.service;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.entity.Appointment;
import com.Diagnostic.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.Diagnostic.utility.ValidationUtil.isNullOrEmpty;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public AppointmentCheckupResponse applyForCheckup(AppointmentCheckupRequest request) {
        Appointment appointment = createAppointmentFromRequest(request);
        boolean hasMissingFields = validateMissingFields(request);
        appointment.setStatus(hasMissingFields ? "In Progress" : "Confirmed");

        appointmentRepository.save(appointment);

        return mapToResponse(appointment);
    }

    @Transactional
    @Override
    public AppointmentCheckupResponse deleteAppointmentById(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus("Cancelled");

        appointmentRepository.delete(appointment);
        return mapToResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse getAppointmentDetailsById(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        return mapToResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse updateAppointment(String appointmentId, AppointmentCheckupRequest request) {
        Appointment appointment = getAppointmentById(appointmentId);

        updateAppointmentFields(appointment, request);
        appointment.setStatus("Confirmed");

        appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    // Helper method to create an Appointment from the request
    private Appointment createAppointmentFromRequest(AppointmentCheckupRequest request) {
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
        return appointment;
    }

    // Helper method to validate missing fields
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

    // Helper method to map Appointment to AppointmentCheckupResponse
    private AppointmentCheckupResponse mapToResponse(Appointment appointment) {
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId(appointment.getAppointmentId());
        response.setPatientName(appointment.getPatientName());
        response.setCheckupType(appointment.getCheckupType());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());
        return response;
    }

    // Helper method to get Appointment by ID
    private Appointment getAppointmentById(String appointmentId) {
        Optional<Appointment> optional = appointmentRepository.findByAppointmentId(appointmentId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Appointment not found with ID: " + appointmentId);
        }
        return optional.get();
    }

    // Helper method to update appointment fields from request
    private void updateAppointmentFields(Appointment appointment, AppointmentCheckupRequest request) {
        appointment.setPatientName(request.getPatientName());
        appointment.setAge(request.getAge());
        appointment.setGender(request.getGender());
        appointment.setMobile(request.getMobile());
        appointment.setEmail(request.getEmail());
        appointment.setCheckupType(request.getCheckupType());
        appointment.setAppointmentDate(request.getPreferredDate());
        appointment.setAppointmentTime(request.getPreferredTime());
    }
}
