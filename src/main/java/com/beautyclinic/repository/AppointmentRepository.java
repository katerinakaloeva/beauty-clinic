package com.beautyclinic.repository;

import com.beautyclinic.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findByCustomer_EmailOrderByAppointmentDateAscStartTimeAsc(
            String email
    );
}