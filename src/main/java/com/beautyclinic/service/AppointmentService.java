package com.beautyclinic.service;

import com.beautyclinic.core.exception.*;
import com.beautyclinic.dto.AppointmentCreateDto;
import com.beautyclinic.dto.AppointmentReadDto;
 import com.beautyclinic.mapper.AppointmentMapper;
 import com.beautyclinic.model.Appointment;
import com.beautyclinic.model.AppointmentStatus;
import com.beautyclinic.model.Treatment;
import com.beautyclinic.repository.AppointmentRepository;
import com.beautyclinic.repository.TreatmentRepository;
import com.beautyclinic.repository.UserAccountRepository;
import com.beautyclinic.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.beautyclinic.dto.CustomerBookingDto;
import com.beautyclinic.model.Role;
import com.beautyclinic.model.UserAccount;

import java.time.LocalDateTime;
import java.time.LocalTime;

 import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TreatmentRepository treatmentRepository;
    private final AppointmentValidator appointmentValidator;
    private final AppointmentMapper appointmentMapper;
     private final UserAccountRepository userAccountRepository;

    public Appointment createAppointment(AppointmentCreateDto dto) {

        appointmentValidator.validate(  dto.getAppointmentDate(),
                dto.getStartTime(),
                dto.getEndTime());

        Treatment treatment = treatmentRepository.findById(dto.getTreatmentId())
                .orElseThrow(() ->
                        new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        if (!treatment.getActive()) {
            throw new InactiveTreatmentException("Η θεραπεία δεν είναι ενεργή");
        }

        List<Appointment> existingAppointments =
                appointmentRepository.findByAppointmentDate(dto.getAppointmentDate());

        for (Appointment existing : existingAppointments) {

            if (existing.getStatus() == AppointmentStatus.CANCELLED) {
                continue;
            }

            boolean overlaps =
                    dto.getStartTime().isBefore(existing.getEndTime())
                            && dto.getEndTime().isAfter(existing.getStartTime());

            if (overlaps) {
                throw new AppointmentConflictException(
                        "Η ώρα που επιλέξατε δεν είναι διαθέσιμη"
                );
            }
        }

        Appointment appointment =
                appointmentMapper.toEntity(dto, treatment);

        return appointmentRepository.save(appointment);
    }

    public Appointment createCustomerBooking(
            CustomerBookingDto dto,
            String customerEmail){

       Treatment treatment = treatmentRepository.findById(dto.getTreatmentId())
               .orElseThrow(() ->
                       new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        if (!treatment.getActive()) {
            throw new InactiveTreatmentException("Η θεραπεία δεν είναι ενεργή");
        }

        LocalTime endTime = dto.getStartTime()
                .plusMinutes(treatment.getDurationMinutes());

        appointmentValidator.validate(dto.getAppointmentDate(), dto.getStartTime(), endTime);
        UserAccount customer = userAccountRepository.findByEmail(customerEmail)
                .orElseThrow(() ->
                        new IllegalStateException("Ο συνδεδεμένος πελάτης δεν βρέθηκε"));

        UserAccount aesthetician = userAccountRepository
                .findFirstByRoleAndActiveTrue(Role.AESTHETICIAN)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Δεν υπάρχει διαθέσιμη ενεργή αισθητικός"
                        )
                );

        List<Appointment> existingAppointments =
                appointmentRepository.findByAppointmentDate(dto.getAppointmentDate());

        for (Appointment existing : existingAppointments) {

            if (existing.getStatus() == AppointmentStatus.CANCELLED) {
                continue;
            }

            boolean overlaps =
                    dto.getStartTime().isBefore(existing.getEndTime())
                            && endTime.isAfter(existing.getStartTime());


            if (overlaps) {
                throw new AppointmentConflictException(
                        "Η ώρα που επιλέξατε δεν είναι διαθέσιμη"
                );
            }

        }
        Appointment appointment = appointmentMapper.toCustomerBookingEntity(
                dto,
                treatment,
                customer,
                aesthetician,
                endTime
        );
        return appointmentRepository.save(appointment);

    }


    public List<AppointmentReadDto> getCustomerAppointments(String customerEmail) {
      return  appointmentRepository
              .findByCustomer_EmailOrderByAppointmentDateAscStartTimeAsc(customerEmail)
               .stream()
              .map(appointmentMapper::toReadDto)
              .toList();


    }



    public List<AppointmentReadDto> getAllAppointments() {

        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toReadDto)
                .toList();
    }

    public void cancelAppointment(Long id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException("Το ραντεβού δεν βρέθηκε"));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Μπορούν να ακυρωθούν μόνο επιβεβαιωμένα ραντεβού"
            );
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.save(appointment);
    }




    public void completeAppointment(Long id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException("Το ραντεβού δεν βρέθηκε"));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Μπορούν να ολοκληρωθούν μόνο επιβεβαιωμένα ραντεβού"
            );
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        appointmentRepository.save(appointment);
    }

    public void cancelCustomerAppointment(Long id, String customerEmail) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException("Το ραντεβού δεν βρέθηκε"));

        if (!appointment.getCustomer().getEmail().equals(customerEmail)) {
            throw new IllegalStateException(
                    "Δεν μπορείτε να ακυρώσετε ραντεβού άλλου πελάτη"
            );
        }
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Μπορούν να ακυρωθούν μόνο επιβεβαιωμένα ραντεβού"
            );
        }
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getStartTime()
        );
        if (!appointmentDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Δεν μπορείτε να ακυρώσετε ραντεβού του οποίου η ώρα έναρξης έχει περάσει"
            );
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.save(appointment);
    }

        public void markNoShow(Long id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException("Το ραντεβού δεν βρέθηκε"));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Μόνο επιβεβαιωμένα ραντεβού μπορούν να σημειωθούν ως μη προσέλευση"
            );
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);

        appointmentRepository.save(appointment);
    }
}
