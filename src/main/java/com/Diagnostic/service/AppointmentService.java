package com.Diagnostic.service;

import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;

public interface AppointmentService {
    AppointmentCheckupResponse applyForCheckup(AppointmentCheckupRequest request);
}
