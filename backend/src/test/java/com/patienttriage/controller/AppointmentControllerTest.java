package com.patienttriage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patienttriage.dto.AppointmentRequest;
import com.patienttriage.dto.AppointmentResponse;
import com.patienttriage.entity.AppointmentStatus;
import com.patienttriage.entity.UserRole;
import com.patienttriage.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AppointmentController.
 */
@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AppointmentService appointmentService;

  @Autowired
  private ObjectMapper objectMapper;

  private AppointmentRequest createAppointmentRequest() {
    AppointmentRequest request = new AppointmentRequest();
    request.setPatientId(1L);
    request.setDoctorId(2L);
    request.setStartDateTime(LocalDateTime.now().plusDays(1));
    request.setReason("Test appointment");
    return request;
  }

  private AppointmentResponse createAppointmentResponse() {
    AppointmentResponse response = new AppointmentResponse();
    response.setAppointmentId(1L);
    response.setPatientId(1L);
    response.setDoctorId(2L);
    response.setAppointmentTime(LocalDateTime.now().plusDays(1));
    response.setStatus(AppointmentStatus.SCHEDULED);
    return response;
  }

  /**
   * Tests successful appointment creation.
   */
  @Test
  void testCreateAppointment_Success() throws Exception {
    // Given
    AppointmentRequest request = createAppointmentRequest();
    when(appointmentService.createAppointment(any(), any(), anyLong())).thenReturn(createAppointmentResponse());

    // When & Then
    mockMvc.perform(post("/api/appointments/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .sessionAttr("userId", 1L)
            .sessionAttr("role", UserRole.PATIENT)
            .sessionAttr("username", "patient@test.com"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Appointment created successfully"));
    
    verify(appointmentService).createAppointment(any(), any(), anyLong());
  }

  /**
   * Tests that unauthenticated users cannot create appointments.
   */
  @Test
  void testCreateAppointment_NotLoggedIn_Returns401() throws Exception {
    // Given
    AppointmentRequest request = createAppointmentRequest();

    // When & Then
    mockMvc.perform(post("/api/appointments/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("Not logged in. Please login first."));
    
    verify(appointmentService, never()).createAppointment(any(), any(), anyLong());
  }

  /**
   * Tests successful retrieval of user's appointments.
   */
  @Test
  void testGetAppointments_Success() throws Exception {
    // Given
    List<AppointmentResponse> appointments = List.of(createAppointmentResponse());
    when(appointmentService.getAppointments(any(), anyLong())).thenReturn(appointments);

    // When & Then
    mockMvc.perform(get("/api/appointments/my")
            .sessionAttr("userId", 1L)
            .sessionAttr("role", UserRole.PATIENT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.appointments").isArray())
        .andExpect(jsonPath("$.count").value(1));
    
    verify(appointmentService).getAppointments(any(), anyLong());
  }

  /**
   * Tests that unauthenticated users cannot retrieve appointments.
   */
  @Test
  void testGetAppointments_NotLoggedIn_Returns401() throws Exception {
    // When & Then
    mockMvc.perform(get("/api/appointments/my"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("Not logged in. Please login first."));
    
    verify(appointmentService, never()).getAppointments(any(), anyLong());
  }

  /**
   * Tests successful retrieval of appointment by ID.
   */
  @Test
  void testGetAppointmentById_Success() throws Exception {
    // Given
    AppointmentResponse response = createAppointmentResponse();
    when(appointmentService.getAppointmentById(anyLong(), any(), anyLong())).thenReturn(response);

    // When & Then
    mockMvc.perform(get("/api/appointments/1")
            .sessionAttr("userId", 1L)
            .sessionAttr("role", UserRole.PATIENT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.appointment.appointmentId").value(1));
    
    verify(appointmentService).getAppointmentById(anyLong(), any(), anyLong());
  }

  /**
   * Tests successful appointment update.
   */
  @Test
  void testUpdateAppointment_Success() throws Exception {
    // Given
    AppointmentRequest request = createAppointmentRequest();
    AppointmentResponse response = createAppointmentResponse();
    when(appointmentService.updateAppointment(anyLong(), any(), any(), anyLong())).thenReturn(response);

    // When & Then
    mockMvc.perform(put("/api/appointments/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .sessionAttr("userId", 1L)
            .sessionAttr("role", UserRole.PATIENT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Appointment updated successfully"));
    
    verify(appointmentService).updateAppointment(anyLong(), any(), any(), anyLong());
  }

  /**
   * Tests successful appointment cancellation.
   */
  @Test
  void testCancelAppointment_Success() throws Exception {
    // Given
    AppointmentResponse response = createAppointmentResponse();
    response.setStatus(AppointmentStatus.CANCELLED);
    when(appointmentService.cancelAppointment(anyLong(), any(), anyLong())).thenReturn(response);

    // When & Then
    mockMvc.perform(delete("/api/appointments/1")
            .sessionAttr("userId", 1L)
            .sessionAttr("role", UserRole.PATIENT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Appointment cancelled successfully"));
    
    verify(appointmentService).cancelAppointment(anyLong(), any(), anyLong());
  }
}

