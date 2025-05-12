package com.Diagnostic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentCheckupRequest {

    @NotNull(message = "Patient name is required")
    private String patientName;
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age can not be negative")
    private int age;
    @NotBlank(message = "Gender is required")
    private String gender;
    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCheckupType() {
        return checkupType;
    }

    public void setCheckupType(String checkupType) {
        this.checkupType = checkupType;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

    public LocalTime getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }

    @NotBlank(message = "Checkup type is required ")
    private String checkupType;

    @NotNull(message = "Preferred date is required")
    private LocalDate preferredDate;
    @NotNull(message = "Preferred time is required")
    private LocalTime preferredTime;
}
