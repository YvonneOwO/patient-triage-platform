package com.patienttriage.repository;

import com.patienttriage.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for PatientProfile entity operations.
 */
@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

  /**
   * Finds a patient profile by patient user ID.
   * 
   * @param patientId the patient user ID
   * @return the PatientProfile entity
   */
  PatientProfile findByPatient_Id(Long patientId);
}
