package com.patienttriage.service;

import com.patienttriage.dto.AppointmentRequest;
import com.patienttriage.dto.AppointmentResponse;
import com.patienttriage.entity.*;
import com.patienttriage.repository.*;
import com.patienttriage.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for AppointmentService.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

  @Mock
  private AppointmentRepository appointmentRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PatientProfileRepository patientProfileRepository;

  @Mock
  private DoctorProfileRepository doctorProfileRepository;

  @InjectMocks
  private AppointmentServiceImpl appointmentService;

  private User patientUser;
  private User doctorUser;
  private User adminUser;
  private Appointment testAppointment;
  private AppointmentRequest appointmentRequest;
  private LocalDateTime futureTime;

  @BeforeEach
  void setUp() {
    futureTime = LocalDateTime.now().plusDays(1);

    // Create test users
    patientUser = new User("patient@test.com", "password", UserRole.PATIENT);
    setUserId(patientUser, 1L);

    doctorUser = new User("doctor@test.com", "password", UserRole.DOCTOR);
    setUserId(doctorUser, 2L);

    adminUser = new User("admin@test.com", "password", UserRole.ADMIN);
    setUserId(adminUser, 3L);

    // Create test appointment
    testAppointment = new Appointment(patientUser, doctorUser, futureTime, "Test reason");
    setAppointmentId(testAppointment, 1L);
    testAppointment.setStatus(AppointmentStatus.SCHEDULED);

    // Create appointment request
    appointmentRequest = new AppointmentRequest();
    appointmentRequest.setPatientId(1L);
    appointmentRequest.setDoctorId(2L);
    appointmentRequest.setStartDateTime(futureTime);
    appointmentRequest.setReason("Test reason");
  }

  /**
   * Tests that patient can create appointment for themselves.
   */
  @Test
  void testCreateAppointment_Patient_CreatesForThemselves() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(patientUser));
    when(userRepository.findById(1L)).thenReturn(Optional.of(patientUser));
    when(userRepository.findById(2L)).thenReturn(Optional.of(doctorUser));
    when(patientProfileRepository.findByPatient_Id(1L)).thenReturn(null);
    when(doctorProfileRepository.findByDoctor_Id(2L)).thenReturn(null);
    when(appointmentRepository.findConflictsByDoctor(anyLong(), any())).thenReturn(new ArrayList<>());
    when(appointmentRepository.findConflictsByPatient(anyLong(), any())).thenReturn(new ArrayList<>());
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

    // When
    AppointmentResponse result = appointmentService.createAppointment(
        appointmentRequest, UserRole.PATIENT, 1L);

    // Then
    assertNotNull(result);
    verify(appointmentRepository).save(any(Appointment.class));
  }

  /**
   * Tests that patient cannot create appointment for other patients.
   */
  @Test
  void testCreateAppointment_Patient_CannotCreateForOthers_ThrowsException() {
    // Given
    appointmentRequest.setPatientId(999L); // Different patient ID
    when(userRepository.findById(1L)).thenReturn(Optional.of(patientUser));

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      appointmentService.createAppointment(appointmentRequest, UserRole.PATIENT, 1L);
    });

    assertEquals("Patients can only create appointments for themselves", exception.getMessage());
    verify(appointmentRepository, never()).save(any(Appointment.class));
  }

  /**
   * Tests that admin can create appointment for any patient and doctor.
   */
  @Test
  void testCreateAppointment_Admin_CanCreateForAnyPatientAndDoctor() {
    // Given
    when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
    when(userRepository.findById(1L)).thenReturn(Optional.of(patientUser));
    when(userRepository.findById(2L)).thenReturn(Optional.of(doctorUser));
    when(patientProfileRepository.findByPatient_Id(1L)).thenReturn(null);
    when(doctorProfileRepository.findByDoctor_Id(2L)).thenReturn(null);
    when(appointmentRepository.findConflictsByDoctor(anyLong(), any())).thenReturn(new ArrayList<>());
    when(appointmentRepository.findConflictsByPatient(anyLong(), any())).thenReturn(new ArrayList<>());
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

    // When
    AppointmentResponse result = appointmentService.createAppointment(
        appointmentRequest, UserRole.ADMIN, 3L);

    // Then
    assertNotNull(result);
    verify(appointmentRepository).save(any(Appointment.class));
  }

  /**
   * Tests that patient only sees their own appointments.
   */
  @Test
  void testGetAppointments_Patient_ReturnsOnlyOwnAppointments() {
    // Given
    List<Appointment> patientAppointments = List.of(testAppointment);
    when(userRepository.findById(1L)).thenReturn(Optional.of(patientUser));
    when(appointmentRepository.findByPatient_Id(1L)).thenReturn(patientAppointments);
    when(patientProfileRepository.findByPatient_Id(1L)).thenReturn(null);
    when(doctorProfileRepository.findByDoctor_Id(2L)).thenReturn(null);

    // When
    List<AppointmentResponse> result = appointmentService.getAppointments(UserRole.PATIENT, 1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(appointmentRepository).findByPatient_Id(1L);
    verify(appointmentRepository, never()).findByDoctor_Id(anyLong());
    verify(appointmentRepository, never()).findAll();
  }

  /**
   * Tests that admin can see all appointments.
   */
  @Test
  void testGetAppointments_Admin_ReturnsAllAppointments() {
    // Given
    List<Appointment> allAppointments = List.of(testAppointment);
    when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
    when(appointmentRepository.findAll()).thenReturn(allAppointments);
    when(patientProfileRepository.findByPatient_Id(1L)).thenReturn(null);
    when(doctorProfileRepository.findByDoctor_Id(2L)).thenReturn(null);

    // When
    List<AppointmentResponse> result = appointmentService.getAppointments(UserRole.ADMIN, 3L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(appointmentRepository).findAll();
  }

  /**
   * Tests that patient can update their own appointment.
   */
  @Test
  void testUpdateAppointment_Patient_CanUpdateOwnAppointment() {
    // Given
    appointmentRequest.setStartDateTime(futureTime.plusHours(2));
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
    when(appointmentRepository.findConflictsByDoctor(anyLong(), any())).thenReturn(new ArrayList<>());
    when(appointmentRepository.findConflictsByPatient(anyLong(), any())).thenReturn(new ArrayList<>());
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
    when(patientProfileRepository.findByPatient_Id(1L)).thenReturn(null);
    when(doctorProfileRepository.findByDoctor_Id(2L)).thenReturn(null);

    // When
    AppointmentResponse result = appointmentService.updateAppointment(
        1L, appointmentRequest, UserRole.PATIENT, 1L);

    // Then
    assertNotNull(result);
    verify(appointmentRepository).save(any(Appointment.class));
  }

  /**
   * Tests that patient cannot change doctor when updating appointment.
   */
  @Test
  void testUpdateAppointment_Patient_CannotChangeDoctor_ThrowsException() {
    // Given
    appointmentRequest.setDoctorId(999L); // Try to change doctor
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      appointmentService.updateAppointment(1L, appointmentRequest, UserRole.PATIENT, 1L);
    });

    assertEquals("You do not have permission to change the doctor for this appointment.", 
        exception.getMessage());
    verify(appointmentRepository, never()).save(any(Appointment.class));
  }

  /**
   * Tests that patient cannot update other patients' appointments.
   */
  @Test
  void testUpdateAppointment_Patient_CannotUpdateOtherPatientAppointment_ThrowsException() {
    // Given
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

    // When & Then - patient with ID 999 trying to update appointment belonging to patient 1
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      appointmentService.updateAppointment(1L, appointmentRequest, UserRole.PATIENT, 999L);
    });

    assertEquals("You do not have permission to update this appointment.", exception.getMessage());
    verify(appointmentRepository, never()).save(any(Appointment.class));
  }

  /**
   * Tests that patient can cancel their own appointment.
   */
  @Test
  void testCancelAppointment_Patient_CanCancelOwnAppointment() {
    // Given
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
    when(patientProfileRepository.findByPatient_Id(1L)).thenReturn(null);
    when(doctorProfileRepository.findByDoctor_Id(2L)).thenReturn(null);

    // When
    AppointmentResponse result = appointmentService.cancelAppointment(1L, UserRole.PATIENT, 1L);

    // Then
    assertNotNull(result);
    assertEquals(AppointmentStatus.CANCELLED, testAppointment.getStatus());
    verify(appointmentRepository).save(any(Appointment.class));
  }

  /**
   * Tests that patient cannot cancel other patients' appointments.
   */
  @Test
  void testCancelAppointment_Patient_CannotCancelOtherPatientAppointment_ThrowsException() {
    // Given
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

    // When & Then - patient with ID 999 trying to cancel appointment belonging to patient 1
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      appointmentService.cancelAppointment(1L, UserRole.PATIENT, 999L);
    });

    assertEquals("You do not have permission to cancel this appointment.", exception.getMessage());
    verify(appointmentRepository, never()).save(any(Appointment.class));
  }

  // Helper methods to set IDs using reflection
  private void setUserId(User user, Long id) {
    try {
      java.lang.reflect.Field idField = User.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(user, id);
    } catch (Exception e) {
      // If reflection fails, continue without ID
    }
  }

  private void setAppointmentId(Appointment appointment, Long id) {
    try {
      java.lang.reflect.Field idField = Appointment.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(appointment, id);
    } catch (Exception e) {
      // If reflection fails, continue without ID
    }
  }
}

