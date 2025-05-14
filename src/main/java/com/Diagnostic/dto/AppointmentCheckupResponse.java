package com.Diagnostic.dto;


import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentCheckupResponse {
    private String appointmentId;
    private String patientName;

    public AppointmentCheckupResponse() {
    }

    private String checkupType;

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getCheckupType() {
        return checkupType;
    }

    public void setCheckupType(String checkupType) {
        this.checkupType = checkupType;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

}
