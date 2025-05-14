package com.Diagnostic.controller;

import com.Diagnostic.Response.CustomResponseModel;
import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("")
    public ResponseEntity<CustomResponseModel<AppointmentCheckupResponse>> apply(
            @Valid @RequestBody AppointmentCheckupRequest request) {
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

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<CustomResponseModel<AppointmentCheckupResponse>> deleteAppointment(@PathVariable String appointmentId) {
        try {
            AppointmentCheckupResponse response = appointmentService.deleteAppointmentById(appointmentId);
            return ResponseEntity.ok(new CustomResponseModel<>(true, "Appointment deleted successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomResponseModel<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<CustomResponseModel<AppointmentCheckupResponse>> getAppointmentDetails(@PathVariable String appointmentId) {
        try {
            AppointmentCheckupResponse response = appointmentService.getAppointmentDetailsById(appointmentId);

            return ResponseEntity.ok(new CustomResponseModel<>(true, "Appointment found successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomResponseModel<>(false, "Error: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<CustomResponseModel<AppointmentCheckupResponse>> updateAppointment(
            @PathVariable String appointmentId,
            @RequestBody AppointmentCheckupRequest request) {
        try {
            AppointmentCheckupResponse response = appointmentService.updateAppointment(appointmentId, request);
            return ResponseEntity.ok(new CustomResponseModel<>(true, "Appointment updated successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomResponseModel<>(false, "Error: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponseModel<>(false, "Internal server error: " + e.getMessage(), null));
        }
    }
}
