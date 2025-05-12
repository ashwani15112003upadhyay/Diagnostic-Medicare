package com.Diagnostic;

import com.Diagnostic.Response.CustomResponseModel;
import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/apply")
    public ResponseEntity<CustomResponseModel<AppointmentCheckupResponse>> apply(
            @RequestBody AppointmentCheckupRequest request) {
        try {
            AppointmentCheckupResponse response = appointmentService.applyForCheckup(request);
            CustomResponseModel<AppointmentCheckupResponse> customResponse = new CustomResponseModel<>(
                    true,
                    "Appointment booked successfully",
                    response
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(customResponse);
        } catch (Exception e) {
            CustomResponseModel<AppointmentCheckupResponse> errorResponse = new CustomResponseModel<>(
                    false,
                    "Error while booking appointment: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
