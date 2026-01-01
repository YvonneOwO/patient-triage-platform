package com.patienttriage.dto;

/**
 * DTO containing full doctor profile information.
 */
public class DoctorInfo {
    
    private Long doctorId;
    private String firstName;
    private String lastName;
    private String specialty;
    private String licenseNumber;
    private String workTime;
    
    // Default constructor
    public DoctorInfo() {}
    
    // Full constructor
    public DoctorInfo(Long doctorId, String firstName, String lastName,
                     String specialty, String licenseNumber, String workTime) {
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
        this.workTime = workTime;
    }
    
    // Getters and Setters
    // doctorId is immutable - no setter provided
    public Long getDoctorId() {
        return doctorId;
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

