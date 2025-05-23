package com.Diagnostic.service;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.entity.Appointment;
import com.Diagnostic.exception.AppointmentNotFoundException;
import com.Diagnostic.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public AppointmentCheckupResponse bookAppointment(AppointmentCheckupRequest request) {
        logger.info("Booking appointment for patient: {}", request.getPatientName());

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

        if (request.getPreferredDate().isAfter(LocalDate.now().plusDays(15))) {
            appointment.setStatus("Pending");
            appointment.setRemark("Choose date within 15 days");
            logger.warn("Appointment date is beyond 15 days for patient: {}", request.getPatientName());
        } else {
            appointment.setStatus("Confirmed");
            appointment.setRemark(null);
            logger.info("Appointment confirmed for patient: {}", request.getPatientName());
        }

        appointmentRepository.save(appointment);
        return toResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse cancelAppointmentById(String appointmentId) {
        logger.info("Cancelling appointment with ID: {}", appointmentId);
        Appointment appointment = getAppointmentByIdInternal(appointmentId);
        appointment.setStatus("Cancelled");
        appointmentRepository.delete(appointment);
        logger.info("Appointment cancelled and deleted: {}", appointmentId);
        return toResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse getAppointmentById(String appointmentId) {
        logger.info("Fetching appointment details for ID: {}", appointmentId);
        Appointment appointment = getAppointmentByIdInternal(appointmentId);
        return toResponse(appointment);
    }

    @Override
    public AppointmentCheckupResponse updateAppointment(String appointmentId, AppointmentCheckupRequest request) {
        logger.info("Updating appointment with ID: {}", appointmentId);
        Appointment appointment = getAppointmentByIdInternal(appointmentId);

        appointment.setPatientName(request.getPatientName());
        appointment.setAge(request.getAge());
        appointment.setGender(request.getGender());
        appointment.setMobile(request.getMobile());
        appointment.setEmail(request.getEmail());
        appointment.setCheckupType(request.getCheckupType());
        appointment.setAppointmentDate(request.getPreferredDate());
        appointment.setAppointmentTime(request.getPreferredTime());

        if (request.getPreferredDate().isAfter(LocalDate.now().plusDays(15))) {
            appointment.setStatus("Pending");
            appointment.setRemark("Choose date within 15 days");
            logger.warn("Updated date is beyond 15 days for appointment: {}", appointmentId);
        } else {
            appointment.setStatus("Confirmed");
            appointment.setRemark(null);
            logger.info("Updated appointment confirmed: {}", appointmentId);
        }

        appointmentRepository.save(appointment);
        return toResponse(appointment);
    }

    @Override
    public List<AppointmentCheckupResponse> getAllAppointments() {
        logger.info("Fetching all appointments");
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

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

    private Appointment getAppointmentByIdInternal(String appointmentId) {
        logger.debug("Looking up appointment by ID: {}", appointmentId);
        return appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));
    }
}
