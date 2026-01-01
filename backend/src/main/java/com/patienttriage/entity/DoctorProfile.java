package com.patienttriage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entity representing a doctor profile with professional information.
 */
@Entity
@Table(name = "doctor_profile")
public class DoctorProfile {
  @Id
  @Column(name = "doctor_id")
  private Long doctorId; // getter only

  @OneToOne
  @MapsId
  @JoinColumn(name = "doctor_id")
  private User doctor; // getter only

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "specialty")
  private String specialty;

  @Column(name = "license_number")
  private String licenseNumber;

  @Column(name = "work_time")
  private String workTime;

  public DoctorProfile() {}

  // TODO: change as needed
  public DoctorProfile(User doctor, String firstName, String lastName, String specialty, String licenseNumber) {
    this.doctor = doctor;
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialty = specialty;
    this.licenseNumber = licenseNumber;
  }

  public Long getDoctorId() {
    return doctorId;
  }

  public User getDoctor() {
    return doctor;
  }

  public void setDoctor(User doctor) {
    this.doctor = doctor;
    this.doctorId = doctor != null ? doctor.getId() : null;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getSpecialty() {
    return specialty;
  }

  public void setSpecialty(String specialty) {
    this.specialty = specialty;
  }

  public String getLicenseNumber() {
    return licenseNumber;
  }

  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber = licenseNumber;
  }

  public String getWorkTime() {
    return workTime;
  }

  public void setWorkTime(String workTime) {
    this.workTime = workTime;
  }
}
