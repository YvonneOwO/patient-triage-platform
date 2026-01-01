package com.patienttriage.repository;

import com.patienttriage.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for DoctorProfile entity operations.
 */
@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

  /**
   * Finds a doctor profile by doctor user ID.
   * 
   * @param doctorId the doctor user ID
   * @return the DoctorProfile entity
   */
  DoctorProfile findByDoctor_Id(Long doctorId);
}
