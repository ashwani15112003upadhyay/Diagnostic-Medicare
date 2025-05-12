package com.Diagnostic.repository;

import com.Diagnostic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentId(String appointmentId);
    void deleteByAppointmentId(String appointmentId);
    boolean existsByAppointmentId(String appointmentId);
}
