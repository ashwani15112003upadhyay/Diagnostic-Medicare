package com.Diagnostic.service;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.entity.Appointment;
import com.Diagnostic.mapper.AppointmentMapper;
import com.Diagnostic.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.Diagnostic.utility.ValidationUtil.isNullOrEmpty;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper mapper;

    @Override
    public AppointmentCheckupResponse applyForCheckup(AppointmentCheckupRequest request) {
        Appointment appointment = mapper.toAppointment(request);
        appointment.setAppointmentId(UUID.randomUUID().toString());

        boolean isDateBeyond15Days = request.getPreferredDate().isAfter(LocalDate.now().plusDays(15));

        boolean hasMissingFields = validateMissingFields(request);

//        appointment.setStatus(hasMissingFields ? "In Progress" : "Confirmed");
//        appointment.setRemark(hasMissingFields ? getMissingFieldMessage(request) : null);

        if (hasMissingFields) {
            appointment.setStatus("In Progress");
            appointment.setRemark(getMissingFieldMessage(request));
        } else if (isDateBeyond15Days) {
            appointment.setStatus("Pending");
            appointment.setRemark("Choose date within 15 days");
        } else {
            appointment.setStatus("Confirmed");
            appointment.setRemark(null);
        }

        appointmentRepository.save(appointment);
        return mapper.toResponse(appointment);
    }

    @Transactional
    @Override
    public AppointmentCheckupResponse deleteAppointmentById(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus("Cancelled");

        appointmentRepository.delete(appointment);
        return mapper.toResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse getAppointmentDetailsById(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        return mapper.toResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse updateAppointment(String appointmentId, AppointmentCheckupRequest request) {
        Appointment appointment = getAppointmentById(appointmentId);

        mapper.updateAppointmentFromRequest(request, appointment);

        boolean hasMissingFields = validateMissingFields(request);
        if (!hasMissingFields) {
            appointment.setStatus("Confirmed");
            appointment.setRemark(null); // Clear remark after successful update
        } else {
            appointment.setStatus("In Progress");
            appointment.setRemark(getMissingFieldMessage(request));
        }

        appointmentRepository.save(appointment);
        return mapper.toResponse(appointment);
    }

    // Helper: Fetch appointment
    private Appointment getAppointmentById(String appointmentId) {
        Optional<Appointment> optional = appointmentRepository.findByAppointmentId(appointmentId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Appointment not found with ID: " + appointmentId);
        }
        return optional.get();
    }

    // Helper: Check for missing fields
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

    // Helper: Build missing field message
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

        return sb.substring(0, sb.length() - 2); // remove trailing comma and space
    }
}
