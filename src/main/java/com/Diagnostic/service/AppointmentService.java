package com.Diagnostic.service;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;

import java.util.List;

public interface AppointmentService {
    AppointmentCheckupResponse applyForCheckup(AppointmentCheckupRequest request);
    AppointmentCheckupResponse deleteAppointmentById(String appointmentId);
    AppointmentCheckupResponse getAppointmentDetailsById(String appointmentId);
    AppointmentCheckupResponse updateAppointment(String appointmentId, AppointmentCheckupRequest request);

    List<AppointmentCheckupResponse> getAllAppointments();
}
