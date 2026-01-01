package com.patienttriage.repository;

import com.patienttriage.entity.Appointment;
import com.patienttriage.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Appointment entity operations.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  /**
   * Finds all appointments for a specific patient.
   * 
   * @param patientId the patient ID
   * @return list of appointments for the patient
   */
  List<Appointment> findByPatient_Id(Long patientId);

  /**
   * Finds all appointments for a specific doctor.
   * 
   * @param doctorId the doctor ID
   * @return list of appointments for the doctor
   */
  List<Appointment> findByDoctor_Id(Long doctorId);

  /**
   * Finds all appointments for a specific patient and doctor.
   * 
   * @param patientId the patient ID
   * @param doctorId the doctor ID
   * @return list of appointments matching both patient and doctor
   */
  List<Appointment> findByPatient_IdAndDoctor_Id(Long patientId, Long doctorId);

  /**
   * Finds an appointment by ID and patient ID.
   * 
   * @param id the appointment ID
   * @param patientId the patient ID
   * @return list of appointments matching both criteria
   */
  List<Appointment> findByIdAndPatient_Id(Long id, Long patientId);

  /**
   * Finds an appointment by ID and doctor ID.
   * 
   * @param id the appointment ID
   * @param doctorId the doctor ID
   * @return list of appointments matching both criteria
   */
  List<Appointment> findByIdAndDoctor_Id(Long id, Long doctorId);

  /**
   * Finds all appointments for a doctor with a specific status.
   * 
   * @param doctorId the doctor ID
   * @param status the appointment status
   * @return list of appointments matching doctor and status
   */
  List<Appointment> findByDoctor_IdAndStatus(Long doctorId, AppointmentStatus status);

  /**
   * Finds appointments with time conflicts for a doctor.
   * 
   * @param doctorId the doctor ID
   * @param time the appointment time to check
   * @return list of conflicting appointments
   */
  @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentTime = :time")
  List<Appointment> findConflictsByDoctor(@Param("doctorId") Long doctorId,
      @Param("time") LocalDateTime time);

  /**
   * Finds appointments with time conflicts for a patient.
   * 
   * @param patientId the patient ID
   * @param time the appointment time to check
   * @return list of conflicting appointments
   */
  @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentTime = :time")
  List<Appointment> findConflictsByPatient(@Param("patientId") Long patientId,
      @Param("time") LocalDateTime time);
}
