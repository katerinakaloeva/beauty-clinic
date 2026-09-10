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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.beautyclinic.dto.CustomerBookingDto;
import com.beautyclinic.model.Role;
import com.beautyclinic.model.UserAccount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TreatmentRepository treatmentRepository;
    private final AppointmentValidator appointmentValidator;
    private final AppointmentMapper appointmentMapper;
     private final UserAccountRepository userAccountRepository;

    @Transactional
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

            boolean overlaps = existing.overlapsWith(
                    dto.getStartTime(),
                    dto.getEndTime()
            );

            if (overlaps) {
                throw new AppointmentConflictException(
                        "Η ώρα που επιλέξατε δεν είναι διαθέσιμη"
                );
            }
        }

        Appointment appointment =
                appointmentMapper.toEntity(dto, treatment);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info(
                "Staff appointment created: appointmentId={}, treatmentId={}, date={}, startTime={}",
                savedAppointment.getId(),
                treatment.getId(),
                dto.getAppointmentDate(),
                dto.getStartTime()
        );

        return savedAppointment;
    }

    @Transactional
    public AppointmentReadDto  createCustomerBooking(
            CustomerBookingDto dto,
            String customerEmail){

       Treatment treatment = treatmentRepository.findById(dto.getTreatmentId())
               .orElseThrow(() ->
                       new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        if (!treatment.getActive()) {
            throw new InactiveTreatmentException("Η θεραπεία δεν είναι ενεργή");
        }

        LocalTime endTime = treatment.calculateEndTime(dto.getStartTime());

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

            boolean overlaps = existing.overlapsWith(
                    dto.getStartTime(),
                    endTime
            );


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
        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info(
                "Customer booking created: appointmentId={}, customerId={}, treatmentId={}, date={}, startTime={}",
                savedAppointment.getId(),
                customer.getId(),
                treatment.getId(),
                dto.getAppointmentDate(),
                dto.getStartTime()
        );

        return appointmentMapper.toReadDto(savedAppointment);

    }

    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableStartTimes(
            Long treatmentId,
            LocalDate appointmentDate) {

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() ->
                        new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        if (!treatment.getActive()) {
            throw new InactiveTreatmentException("Η θεραπεία δεν είναι ενεργή");
        }

        if (appointmentDate.isBefore(LocalDate.now())) {
            return List.of();
        }

        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(18, 0);

        List<Appointment> existingAppointments =
                appointmentRepository.findByAppointmentDate(appointmentDate);

        List<LocalTime> availableTimes = new ArrayList<>();

        for (LocalTime startTime = openingTime;
             startTime.isBefore(closingTime);
             startTime = startTime.plusHours(1)) {

            LocalTime endTime = startTime.plusMinutes(
                    treatment.getDurationMinutes()
            );

             if (endTime.isAfter(closingTime)) {
                continue;
            }

             if (appointmentDate.isEqual(LocalDate.now())
                    && !startTime.isAfter(LocalTime.now())) {
                continue;
            }

            boolean overlaps = false;

            for (Appointment existing : existingAppointments) {
                if (existing.getStatus() == AppointmentStatus.CANCELLED) {
                    continue;
                }

                if (existing.overlapsWith(startTime, endTime)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                availableTimes.add(startTime);
            }
        }

        return availableTimes;
    }


    @Transactional(readOnly = true)
    public List<AppointmentReadDto> getCustomerAppointments(String customerEmail) {
      return  appointmentRepository
              .findByCustomer_EmailOrderByAppointmentDateAscStartTimeAsc(customerEmail)
               .stream()
              .map(appointmentMapper::toReadDto)
              .toList();


    }



    @Transactional(readOnly = true)
    public List<AppointmentReadDto> getAllAppointments() {

        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toReadDto)
                .toList();
    }

    @Transactional
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

        log.info("Appointment cancelled by staff: appointmentId={}", appointment.getId());
    }




    @Transactional
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

    @Transactional
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

        log.info(
                "Appointment cancelled by customer: appointmentId={}, customerId={}",
                appointment.getId(),
                appointment.getCustomer().getId()
        );
    }

    @Transactional
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
