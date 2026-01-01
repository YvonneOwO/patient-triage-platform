package com.patienttriage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entity representing an appointment between a patient and a doctor.
 */
@Entity
@Table(name = "appointments")
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // appointment id, different appointment have different id
  private Long id;

  /**
   * Use LAZY loading to avoid unnecessary fetching of related User entities.
   *
   * Appointment only needs patient/doctor details when explicitly accessed,
   * so delaying the database query improves performance and reduces memory usage.
   *
   * This also prevents potential circular loading (User -> Appointments -> User...)
   * and is the recommended practice for @ManyToOne associations.
   */
  // patient
  // this is a User entity, so when we use JPA repository, we need to write the function name as
  // findByPatient_Id to find the appointment related patientId. The underscore _ navigates to a
  // property of the related entity
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "patient_id", nullable = false)
  private User patient;

  // doctor
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "doctor_id", nullable = false)
  private User doctor;

  @Column(name = "appointment_time", nullable = false)
  private LocalDateTime appointmentTime;

  @Column(name = "reason")
  private String reason;

  @Enumerated(EnumType.STRING)
  private AppointmentStatus status = AppointmentStatus.SCHEDULED;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  // Constructors
  public Appointment() {}

  public Appointment(User user, User doctor, LocalDateTime appointmentTime, String reason) {
    this.patient = user;
    this.doctor = doctor;
    this.appointmentTime = appointmentTime;
    this.reason = reason;
  }

  // the id will be unique and cannot be changed, so we only have getter
  public Long getId() {
    return id;
  }

  public User getDoctor() {
    return doctor;
  }

  public void setDoctor(User doctor) {
    this.doctor = doctor;
  }

  public User getPatient() {
    return patient;
  }

  public void setPatient(User patient) {
    this.patient = patient;
  }

  public LocalDateTime getAppointmentTime() {
    return appointmentTime;
  }

  public void setAppointmentTime(LocalDateTime appointmentTime) {
    this.appointmentTime = appointmentTime;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public AppointmentStatus getStatus() {
    return status;
  }

  public void setStatus(AppointmentStatus status) {
    this.status = status;
  }

  // we only can check when did the appointment made, so we only have getter function for this
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

}
