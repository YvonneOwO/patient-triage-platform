package com.patienttriage.dto;

import com.patienttriage.entity.AppointmentStatus;
import java.time.LocalDateTime;

/**
 * DTO for returning appointment details to the client.
 */
public class AppointmentResponse {

  // will always present - basic appointment info
  private Long appointmentId;
  private Long patientId;
  private Long doctorId;
  private LocalDateTime appointmentTime;
  private String reason;
  private AppointmentStatus status;
  private LocalDateTime createdAt;

  // optional, based on the role
  private PatientInfo patientInfo; // null for patient, full for DOCTOR/ADMIN
  private DoctorInfo doctorInfo; // DoctorInfo for DOCTOR/ADMIN
  private LimitedDoctorInfo limitedDoctorInfo; //LimitedDoctorInfo for PATIENT

  // Default constructor
  public AppointmentResponse() {}

  // basic appointment info for all roles
  public AppointmentResponse(Long appointmentId, Long patientId, Long doctorId,
      LocalDateTime appointmentTime, String reason, AppointmentStatus status, LocalDateTime createdAt) {
    this.appointmentId = appointmentId;
    this.patientId = patientId;
    this.doctorId = doctorId;
    this.appointmentTime = appointmentTime;
    this.reason = reason;
    this.status = status;
    this.createdAt = createdAt;
  }

  // Full constructor with nested objects
  public AppointmentResponse(Long appointmentId, Long patientId, Long doctorId,
      LocalDateTime appointmentTime, String reason, AppointmentStatus status, LocalDateTime createdAt,
      PatientInfo patientInfo, DoctorInfo doctorInfo, LimitedDoctorInfo limitedDoctorInfo) {
    this.appointmentId = appointmentId;
    this.patientId = patientId;
    this.doctorId = doctorId;
    this.appointmentTime = appointmentTime;
    this.reason = reason;
    this.status = status;
    this.createdAt = createdAt;
    this.patientInfo = patientInfo;
    this.doctorInfo = doctorInfo;
    this.limitedDoctorInfo = limitedDoctorInfo;
  }

  // Getters and Setters
  public Long getAppointmentId() {
    return appointmentId;
  }

  public void setAppointmentId(Long appointmentId) {this.appointmentId = appointmentId;}

  public Long getPatientId() {
    return patientId;
  }

  public void setPatientId(Long patientId) { this.patientId = patientId;}

  public Long getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(Long doctorId) {this.doctorId = doctorId;}

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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public PatientInfo getPatientInfo() {
    return patientInfo;
  }

  public void setPatientInfo(PatientInfo patientInfo) {
    this.patientInfo = patientInfo;
  }

  public DoctorInfo getDoctorInfo() {
    return doctorInfo;
  }

  public void setDoctorInfo(DoctorInfo doctorInfo) {
    this.doctorInfo = doctorInfo;
  }

  public LimitedDoctorInfo getLimitedDoctorInfo() {
    return limitedDoctorInfo;
  }

  public void setLimitedDoctorInfo(LimitedDoctorInfo limitedDoctorInfo) {
    this.limitedDoctorInfo = limitedDoctorInfo;
  }

  // Helper methods for convenience
  public boolean hasPatientInfo() {
    return patientInfo != null;
  }

  public boolean hasDoctorInfo() {
    return doctorInfo != null;
  }

  public boolean hasLimitedDoctorInfo() {
    return limitedDoctorInfo != null;
  }
}
