package com.Diagnostic.service;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;

import java.util.List;

public interface AppointmentService {
    AppointmentCheckupResponse bookAppointment(AppointmentCheckupRequest request);
    AppointmentCheckupResponse cancelAppointmentById(String appointmentId);
    AppointmentCheckupResponse getAppointmentById(String appointmentId);
    AppointmentCheckupResponse updateAppointment(String appointmentId, AppointmentCheckupRequest request);

    List<AppointmentCheckupResponse> getAllAppointments();
}
