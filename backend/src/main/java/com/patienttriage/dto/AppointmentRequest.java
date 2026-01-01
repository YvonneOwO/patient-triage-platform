package com.patienttriage.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for receiving appointment data from the client.
 */
public class AppointmentRequest {

  @NotNull(message = "Patient ID is required")
  private Long patientId;

  @NotNull(message = "Doctor ID is required")
  private Long doctorId;

  @NotNull(message = "Appointment time is required")
  @Future(message = "Appointment time must be in the future")
  private LocalDateTime appointmentTime; // frontend must use format like: "2025-11-21T14:00:00"

  private String reason; // Optional field

  public Long getPatientId() {
    return patientId;
  }

  public void setPatientId(Long patientId) {
    this.patientId = patientId;
  }

  public Long getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(Long doctorId) {
    this.doctorId = doctorId;
  }

  public LocalDateTime getAppointmentTime() {
    return appointmentTime;
  }

  public void setStartDateTime(LocalDateTime appointmentTime) {
    this.appointmentTime = appointmentTime;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
