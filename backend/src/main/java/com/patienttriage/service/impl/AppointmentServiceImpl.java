package com.patienttriage.service.impl;

import com.patienttriage.dto.AppointmentRequest;
import com.patienttriage.dto.AppointmentResponse;
import com.patienttriage.dto.DoctorInfo;
import com.patienttriage.dto.LimitedDoctorInfo;
import com.patienttriage.dto.PatientInfo;
import com.patienttriage.entity.Appointment;
import com.patienttriage.entity.AppointmentStatus;
import com.patienttriage.entity.DoctorProfile;
import com.patienttriage.entity.PatientProfile;
import com.patienttriage.entity.User;
import com.patienttriage.entity.UserRole;
import com.patienttriage.repository.AppointmentRepository;
import com.patienttriage.repository.UserRepository;
import com.patienttriage.repository.PatientProfileRepository;
import com.patienttriage.repository.DoctorProfileRepository;
import com.patienttriage.service.AppointmentService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AppointmentService for appointment management operations.
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final UserRepository userRepository;
  private final PatientProfileRepository patientProfileRepository;
  private final DoctorProfileRepository doctorProfileRepository;

  public AppointmentServiceImpl(AppointmentRepository appointmentRepository, 
                                UserRepository userRepository, PatientProfileRepository patientProfileRepository, DoctorProfileRepository doctorProfileRepository) {
    this.appointmentRepository = appointmentRepository;
    this.userRepository = userRepository;
    this.patientProfileRepository = patientProfileRepository;
    this.doctorProfileRepository = doctorProfileRepository;
  }

  // ------------- Create appointments -------------- //
  /**
   * Creates a new appointment.
   * 
   * @param request the appointment request containing patientId, doctorId, appointmentTime, and reason
   * @param role the role of the user making the request (ADMIN, DOCTOR, PATIENT)
   * @param currentUserId the ID of the user making the request
   * @return AppointmentResponse with role-appropriate information
   */
  @Override
  @Transactional
  public AppointmentResponse createAppointment(AppointmentRequest request, UserRole role, Long currentUserId) {
    // 1. Load and validate current logged-in user exists
    // currentUserId comes from HttpSession (set during login in UserController)
    User currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + currentUserId));

    // 2. Validate role-based constraints
    // Ensure the logged-in user can only create appointments for themselves
    UserRole currentUserRole = currentUser.getRole();
    
    if (currentUserRole == UserRole.PATIENT) {
      // PATIENT can only create appointments for themselves
      if (!request.getPatientId().equals(currentUserId)) {
        throw new RuntimeException("Patients can only create appointments for themselves");
      }
    } else if (currentUserRole == UserRole.DOCTOR) {
      // DOCTOR can only create appointments for themselves
      if (!request.getDoctorId().equals(currentUserId)) {
        throw new RuntimeException("Doctors can only create appointments for themselves");
      }
    } else if (currentUserRole == UserRole.ADMIN) {
      // ADMIN can create appointments for any patient and doctor (no restrictions)
      // No validation needed - admin has full access
    }

    // 3. Validate patient exists and has PATIENT role
    User patient= userRepository.findById(request.getPatientId())
        .orElseThrow(() -> new RuntimeException("Patient not found with id: " + request.getPatientId()));
    
    if (patient.getRole() != UserRole.PATIENT) {
      throw new RuntimeException("User with id " + request.getPatientId() + " is not a patient");
    }

    // 4. Validate doctor exists and has DOCTOR role
    User doctor = userRepository.findById(request.getDoctorId())
        .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getDoctorId()));
    
    if (doctor.getRole() != UserRole.DOCTOR) {
      throw new RuntimeException("User with id " + request.getDoctorId() + " is not a doctor");
    }

    // 5. Validate appointment time is in the future
    LocalDateTime appointmentTime = request.getAppointmentTime();
    if (appointmentTime.isBefore(LocalDateTime.now())) {
      throw new RuntimeException("Appointment time must be in the future");
    }

    // 6. Check for time conflicts (doctor and patient cannot have overlapping appointments)
    checkTimeConflicts(
        appointmentTime,
        request.getDoctorId(),
        request.getPatientId(),
        null // No appointment to ignore for new appointments
    );

    // 7. Create appointment entity
    Appointment appointment = new Appointment(
        patient,
        doctor,
        appointmentTime,
        request.getReason()
    );

    // 8. Save appointment
    Appointment savedAppointment = appointmentRepository.save(appointment);
    return toResponse(savedAppointment, currentUserRole);
  }

  // ------------- Get appointments -------------- //
  /**
   * Retrieves all appointments based on user role.
   * 
   * @param role the role of the user making the request (ADMIN, DOCTOR, PATIENT)
   * @param currentUserId the ID of the user making the request
   * @return List of AppointmentResponse with role-appropriate information
   */
  @Override
  public List<AppointmentResponse> getAppointments(UserRole role, Long currentUserId) {
    // 1. load current user
    User currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + currentUserId));

    List<Appointment> appointments;

    // 2. role-based query
    switch (role) {
      case PATIENT:
        appointments = appointmentRepository.findByPatient_Id(currentUserId);
        break;
      case DOCTOR:
        appointments = appointmentRepository.findByDoctor_Id(currentUserId);
        break;
      case ADMIN:
        appointments = appointmentRepository.findAll();
        break;
      default:
        throw new RuntimeException("Invalid role");
    }

    // 3. map to responses
    return appointments.stream()
        .map(a -> toResponse(a, role))
        .toList();
  }

  /**
   * Retrieves a single appointment by ID.
   * 
   * @param appointmentId the ID of the appointment to retrieve
   * @param role the role of the user making the request (ADMIN, DOCTOR, PATIENT)
   * @param currentUserId the ID of the user making the request
   * @return AppointmentResponse with role-appropriate information
   */
  @Override
  public AppointmentResponse getAppointmentById(Long appointmentId, UserRole role, Long currentUserId) {
    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

    if (!hasAccess(appointment, role, currentUserId)) {
      throw new IllegalArgumentException("You do not have permission to view this appointment.");
    }

    return toResponse(appointment, role);
  }


  // ------------- Update appointments -------------- //
  /**
   * Updates an existing appointment.
   * 
   * @param appointmentId the ID of the appointment to update
   * @param request the appointment request containing updated fields
   * @param role the role of the user making the request (ADMIN, DOCTOR, PATIENT)
   * @param currentUserId the ID of the user making the request
   * @return AppointmentResponse with updated information
   */
  @Override
  public AppointmentResponse updateAppointment(Long appointmentId, AppointmentRequest request, UserRole role, Long currentUserId) {
      Appointment appointment = appointmentRepository.findById(appointmentId)
          .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

      if (!hasAccess(appointment, role, currentUserId)) {
        throw new IllegalArgumentException("You do not have permission to update this appointment.");
      }

      // Cannot update if cancelled / completed
      if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
          appointment.getStatus() == AppointmentStatus.COMPLETED) {
        throw new IllegalArgumentException("Cannot update cancelled or completed appointments.");
      }

      // Check if doctor or patient is being changed
      boolean doctorChanged = !request.getDoctorId().equals(appointment.getDoctor().getId());
      boolean patientChanged = !request.getPatientId().equals(appointment.getPatient().getId());

      // PATIENT and DOCTOR cannot change doctor/patient assignments
      if (role != UserRole.ADMIN) {
        if (doctorChanged) {
          throw new IllegalArgumentException("You do not have permission to change the doctor for this appointment.");
        }
        if (patientChanged) {
          throw new IllegalArgumentException("You do not have permission to change the patient for this appointment.");
        }
      }

      // Admin can change doctor and patient - validate new ones exist and have correct roles
      if (role == UserRole.ADMIN) {
        if (doctorChanged) {
          User newDoctor = userRepository.findById(request.getDoctorId())
              .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getDoctorId()));
          
          if (newDoctor.getRole() != UserRole.DOCTOR) {
            throw new RuntimeException("User with id " + request.getDoctorId() + " is not a doctor");
          }
          appointment.setDoctor(newDoctor);
        }

        if (patientChanged) {
          User newPatient = userRepository.findById(request.getPatientId())
              .orElseThrow(() -> new RuntimeException("Patient not found with id: " + request.getPatientId()));
          
          if (newPatient.getRole() != UserRole.PATIENT) {
            throw new RuntimeException("User with id " + request.getPatientId() + " is not a patient");
          }
          appointment.setPatient(newPatient);
        }
      }

      // Determine final doctor and patient IDs for conflict check
      // (after any admin changes, use the final values)
      Long finalDoctorId = appointment.getDoctor().getId();
      Long finalPatientId = appointment.getPatient().getId();

      // Check for time conflicts with the final doctor and patient
      // (ignore this appointment itself since we're updating it)
      checkTimeConflicts(
          request.getAppointmentTime(),
          finalDoctorId,
          finalPatientId,
          appointmentId // Ignore this appointment when checking conflicts
      );

      // Update time and reason (all roles can update these)
      appointment.setAppointmentTime(request.getAppointmentTime());
      appointment.setReason(request.getReason());

      appointmentRepository.save(appointment);
      return toResponse(appointment, role);
    }


  // ------------- Cancel appointments -------------- //
  /**
   * Cancels an appointment by setting status to CANCELLED.
   * 
   * @param appointmentId the ID of the appointment to cancel
   * @param role the role of the user making the request (ADMIN, DOCTOR, PATIENT)
   * @param currentUserId the ID of the user making the request
   * @return AppointmentResponse with cancelled status
   */
  @Override
  public AppointmentResponse cancelAppointment(Long appointmentId, UserRole role, Long currentUserId) {
    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

    if (!hasAccess(appointment, role, currentUserId)) {
      throw new IllegalArgumentException("You do not have permission to cancel this appointment.");
    }

    appointment.setStatus(AppointmentStatus.CANCELLED);
    appointmentRepository.save(appointment);
    return toResponse(appointment, role);
  }

  // ========================================================================
  // Helper Methods
  // ========================================================================

  /** Check doctor + patient time conflicts **/
  private void checkTimeConflicts(LocalDateTime time, Long doctorId, Long patientId, Long ignoreAppointmentId) {
    // doctor conflicts
    List<Appointment> doctorConflicts =
        appointmentRepository.findConflictsByDoctor(doctorId, time);

    // ignore appointment itself when updating
    if (ignoreAppointmentId != null) {
      doctorConflicts = doctorConflicts.stream()
          .filter(a -> !a.getId().equals(ignoreAppointmentId))
          .toList();
    }

    if (!doctorConflicts.isEmpty()) {
      throw new IllegalArgumentException("Doctor already has an appointment at this time.");
    }

    // patient conflicts
    List<Appointment> patientConflicts =
        appointmentRepository.findConflictsByPatient(patientId, time);

    if (ignoreAppointmentId != null) {
      patientConflicts = patientConflicts.stream()
          .filter(a -> !a.getId().equals(ignoreAppointmentId))
          .toList();
    }

    if (!patientConflicts.isEmpty()) {
      throw new IllegalArgumentException("Patient already has an appointment at this time.");
    }
  }

  /** Role access check **/
  private boolean hasAccess(Appointment appointment, UserRole role, Long currentUserId) {
    return switch (role) {
      case ADMIN -> true;
      case DOCTOR -> appointment.getDoctor().getId().equals(currentUserId);
      case PATIENT -> appointment.getPatient().getId().equals(currentUserId);
    };
  }

  /** Convert Appointment entity to DTO with role-based visibility **/
  private AppointmentResponse toResponse(Appointment appointment, UserRole role) {

    AppointmentResponse dto = new AppointmentResponse();
    dto.setAppointmentId(appointment.getId());
    dto.setAppointmentTime(appointment.getAppointmentTime());
    dto.setReason(appointment.getReason());
    dto.setStatus(appointment.getStatus());
    dto.setCreatedAt(appointment.getCreatedAt());

    // Always include basic IDs
    Long patientUserId = appointment.getPatient().getId();
    Long doctorUserId = appointment.getDoctor().getId();

    // backend -> frontend
    dto.setPatientId(patientUserId);
    dto.setDoctorId(doctorUserId);

    // Load full profiles (may be null if profiles don't exist yet)
    PatientProfile patientProfile = patientProfileRepository.findByPatient_Id(patientUserId);
    DoctorProfile doctorProfile = doctorProfileRepository.findByDoctor_Id(doctorUserId);

    switch (role) {

      case ADMIN:
      case DOCTOR:
        // 1. Full Patient Info (handle null profile)
        if (patientProfile != null) {
          dto.setPatientInfo(new PatientInfo(
              patientProfile.getPatientId(),
              patientProfile.getFirstName(),
              patientProfile.getLastName(),
              patientProfile.getAge(),
              patientProfile.getGender(),
              patientProfile.getSymptom(),
              patientProfile.getMedicalHistory(),
              patientProfile.getAllergies(),
              patientProfile.getCurrentMedications(),
              patientProfile.getTriagePriority()
          ));
        } else {
          // Patient profile doesn't exist - set with default values
          dto.setPatientInfo(new PatientInfo(
              patientUserId,
              null, // firstName
              null, // lastName
              0,    // age
              null, // gender
              null, // symptom
              null, // medicalHistory
              null, // allergies
              null, // currentMedications
              null  // triagePriority
          ));
        }

        // 2. Full Doctor Info (handle null profile)
        if (doctorProfile != null) {
          dto.setDoctorInfo(new DoctorInfo(
              doctorProfile.getDoctorId(),
              doctorProfile.getFirstName(),
              doctorProfile.getLastName(),
              doctorProfile.getSpecialty(),
              doctorProfile.getLicenseNumber(),
              doctorProfile.getWorkTime()
          ));
        } else {
          // Doctor profile doesn't exist - set with default values
          dto.setDoctorInfo(new DoctorInfo(
              doctorUserId,
              null, // firstName
              null, // lastName
              null, // specialty
              null, // licenseNumber
              null  // workTime
          ));
        }
        break;

      case PATIENT:
        // Patient sees only limited doctor info (handle null profile)
        if (doctorProfile != null) {
          dto.setLimitedDoctorInfo(new LimitedDoctorInfo(
              doctorProfile.getFirstName(),
              doctorProfile.getLastName(),
              doctorProfile.getSpecialty()
          ));
        } else {
          // Doctor profile doesn't exist - set with default values
          dto.setLimitedDoctorInfo(new LimitedDoctorInfo(
              null, // firstName
              null, // lastName
              null  // specialty
          ));
        }
        break;
    }

    return dto;
  }

}
