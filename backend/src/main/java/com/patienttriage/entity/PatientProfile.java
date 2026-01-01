package com.patienttriage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entity representing a patient profile with medical information.
 */
@Entity
@Table(name = "patient_profile")
public class PatientProfile {
  @Id
  @Column(name = "patient_id")
  private Long patientId; // getter only

  @OneToOne
  @MapsId
  @JoinColumn(name = "patient_id")
  private User patient;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "age")
  private int age;

  @Column(name = "gender", length = 20)
  private String gender;

  @Column(name = "symptom")
  private String symptom;

  @Column(name = "medical_history")
  private String medicalHistory;

  @Column(name = "allergies")
  private String allergies;

  @Column(name = "current_medications")
  private String currentMedications;

  @Column(name = "triage_priority", length = 20)
  private String triagePriority;

  // default constructor
  public PatientProfile() {}

  // Constructor: ensure IDs are synced
  public PatientProfile(User patient) {
    this.patient = patient;
    this.patientId = patient.getId();
  }

  public Long getPatientId() {
    return patientId;
  }

  public void setPatientId(Long patientId) {
    this.patientId = patientId;
  }

  public User getPatient() {
    return patient;
  }

  public void setPatient(User patient) {
    this.patient = patient;
    this.patientId = patient != null ? patient.getId() : null;
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

  public String getSymptom() {
    return symptom;
  }

  public void setSymptom(String symptom) {
    this.symptom = symptom;
  }

  public String getMedicalHistory() {
    return medicalHistory;
  }

  public void setMedicalHistory(String medicalHistory) {
    this.medicalHistory = medicalHistory;
  }

  public String getAllergies() {
    return allergies;
  }

  public void setAllergies(String allergies) {
    this.allergies = allergies;
  }

  public String getCurrentMedications() {
    return currentMedications;
  }

  public void setCurrentMedications(String currentMedications) {
    this.currentMedications = currentMedications;
  }

  public String getTriagePriority() {
    return triagePriority;
  }

  public void setTriagePriority(String triagePriority) {
    this.triagePriority = triagePriority;
  }
}
