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

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;
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
        appointment.setStatus("Confirmed");

        appointmentRepository.save(appointment);

        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId(appointment.getAppointmentId());
        response.setPatientName(appointment.getPatientName());
        response.setCheckupType(appointment.getCheckupType());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());

        return response;
    }

    @Transactional
    @Override
    public AppointmentCheckupResponse deleteAppointmentById(String appointmentId) {
        Optional<Appointment> optional = appointmentRepository.findByAppointmentId(appointmentId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Appointment not found with ID: " + appointmentId);
        }

        Appointment appointment = optional.get();

        // Update status to "Cancelled" before deletion
        appointment.setStatus("Cancelled");


        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId(appointment.getAppointmentId());
        response.setPatientName(appointment.getPatientName());
        response.setCheckupType(appointment.getCheckupType());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());

        appointmentRepository.delete(appointment);
        return response;
    }

    @Override
    public AppointmentCheckupResponse getAppointmentDetailsById(String appointmentId) {
        try {
            // Find the appointment by appointmentId
            Optional<Appointment> appointmentOptional = appointmentRepository.findByAppointmentId(appointmentId);

            if (appointmentOptional.isPresent()) {
                Appointment appointment = appointmentOptional.get();

                AppointmentCheckupResponse response = new AppointmentCheckupResponse();
                response.setAppointmentId(appointment.getAppointmentId());
                response.setPatientName(appointment.getPatientName());
                response.setCheckupType(appointment.getCheckupType());
                response.setAppointmentDate(appointment.getAppointmentDate());
                response.setAppointmentTime(appointment.getAppointmentTime());
                response.setStatus(appointment.getStatus());

                return response;
            } else {
                // If appointment not found, throw an exception
                throw new RuntimeException("Appointment not found with ID: " + appointmentId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching appointment details: " + e.getMessage());
        }
    }

    @Override
    public AppointmentCheckupResponse updateAppointment(String appointmentId, AppointmentCheckupRequest request) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findByAppointmentId(appointmentId);

        if (optionalAppointment.isEmpty()) {
            throw new RuntimeException("Appointment not found with ID: " + appointmentId);
        }

        Appointment appointment = optionalAppointment.get();

        // Update fields
        appointment.setPatientName(request.getPatientName());
        appointment.setAge(request.getAge());
        appointment.setGender(request.getGender());
        appointment.setMobile(request.getMobile());
        appointment.setEmail(request.getEmail());
        appointment.setCheckupType(request.getCheckupType());
        appointment.setAppointmentDate(request.getPreferredDate());
        appointment.setAppointmentTime(request.getPreferredTime());
        appointment.setStatus("Confirmed");

        // Save updated entity
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // Prepare response
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId(updatedAppointment.getAppointmentId());
        response.setPatientName(updatedAppointment.getPatientName());
        response.setCheckupType(updatedAppointment.getCheckupType());
        response.setAppointmentDate(updatedAppointment.getAppointmentDate());
        response.setAppointmentTime(updatedAppointment.getAppointmentTime());
        response.setStatus(updatedAppointment.getStatus());

        return response;
    }

}
