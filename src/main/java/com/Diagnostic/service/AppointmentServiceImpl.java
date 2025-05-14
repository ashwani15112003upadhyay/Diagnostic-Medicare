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

        // Set status based on missing fields
        String status = hasMissingFields ? "In Progress" : "Confirmed";
        appointment.setStatus(status);

        // Set remark if any field is missing
        String remark = hasMissingFields ? getMissingFieldMessage(request) : null;

        // If status is "Confirmed", do not include the remark
        if ("Confirmed".equals(status)) {
            remark = null;  // Remove remark for confirmed status
        }

        // Set the remark in the appointment
        appointment.setRemark(remark);

        // Save appointment
        appointmentRepository.save(appointment);

        // Return response
        return mapToResponse(appointment);
    }

    @Transactional
    @Override
    public AppointmentCheckupResponse deleteAppointmentById(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus("Cancelled");

        // Save the cancelled status
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

        // Update the appointment fields from the request
        updateAppointmentFields(appointment, request);

        // Check if all required fields are set
        boolean hasMissingFields = validateMissingFields(request);
        if (!hasMissingFields) {
            // If no fields are missing, set the status to "Confirmed"
            appointment.setStatus("Confirmed");
            // Clear the remark as no fields are missing
            appointment.setRemark(null);
        }

        // Save updated appointment
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

    // Helper method to generate the missing field message
    private String getMissingFieldMessage(AppointmentCheckupRequest request) {
        if (isNullOrEmpty(request.getPatientName())) {
            return "Patient Name is missing";
        } else if (request.getAge() <= 0) {
            return "Age is missing";
        } else if (isNullOrEmpty(request.getGender())) {
            return "Gender is missing";
        } else if (isNullOrEmpty(request.getMobile())) {
            return "Mobile is missing";
        } else if (isNullOrEmpty(request.getEmail())) {
            return "Email is missing";
        } else if (isNullOrEmpty(request.getCheckupType())) {
            return "Checkup Type is missing";
        } else if (request.getPreferredDate() == null) {
            return "Appointment Date is missing";
        } else if (request.getPreferredTime() == null) {
            return "Appointment Time is missing";
        }
        return "Missing field(s)";
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
        response.setRemark(appointment.getRemark()); // Set the remark here

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
