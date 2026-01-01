package com.patienttriage.dto;

/**
 * DTO containing full patient profile information.
 */
public class PatientInfo {
    PatientInfo patientInfo;

    public PatientInfo(PatientInfo patientInfo) {
        this.patientInfo = patientInfo;
    }
    public PatientInfo getPatientInfo() {
        return patientInfo;
    }
    private Long patientId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String symptom;
    private String medicalHistory;
    private String allergies;
    private String currentMedications;
    private String triagePriority;
    
    // Default constructor
    
    // Full constructor
    public PatientInfo(Long patientId, String firstName, String lastName, int age,
                       String gender, String symptom, String medicalHistory,
                       String allergies, String currentMedications, String triagePriority) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.symptom = symptom;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
        this.currentMedications = currentMedications;
        this.triagePriority = triagePriority;
    }
    
    // Getters and Setters
    public void setPatientInfo(PatientInfo patientInfo) {
      this.patientInfo = patientInfo;
    }

    public Long getPatientId() {
        return patientId;
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
