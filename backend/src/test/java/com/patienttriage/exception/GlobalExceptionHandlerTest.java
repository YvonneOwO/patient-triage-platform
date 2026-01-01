package com.patienttriage.exception;

import com.patienttriage.controller.AppointmentController;
import com.patienttriage.controller.UserController;
import com.patienttriage.dto.UserRegisterRequest;
import com.patienttriage.entity.UserRole;
import com.patienttriage.service.AppointmentService;
import com.patienttriage.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for GlobalExceptionHandler.
 */
@WebMvcTest(controllers = {UserController.class, AppointmentController.class})
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private AppointmentService appointmentService;

  /**
   * Tests validation exception handling returns 400 with field errors.
   */
  @Test
  void testHandleValidationException_Returns400WithFieldErrors() throws Exception {
    // Given - invalid request (missing required fields)
    // Test with AppointmentController which has @Valid on createAppointment
    String invalidJson = "{}";

    // When & Then - test with appointment endpoint that has @Valid
    mockMvc.perform(post("/api/appointments/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson)
            .sessionAttr("userId", 1L)
            .sessionAttr("role", UserRole.PATIENT))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"))
        .andExpect(jsonPath("$.fieldErrors").exists());
  }

  /**
   * Tests runtime exception handling returns 500 with error message.
   */
  @Test
  void testHandleRuntimeException_Returns500WithErrorMessage() throws Exception {
    // Given
    when(userService.register(anyString(), anyString(), any(UserRole.class)))
        .thenThrow(new RuntimeException("Username already exists"));

    UserRegisterRequest request = new UserRegisterRequest();
    request.setUsername("test@test.com");
    request.setPassword("password123");
    request.setRole(UserRole.PATIENT);

    // When & Then
    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"test@test.com\",\"password\":\"password123\",\"role\":\"PATIENT\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal Server Error"))
        .andExpect(jsonPath("$.message").value("Username already exists"));
  }

  /**
   * Tests invalid password exception handling returns 500.
   */
  @Test
  void testHandleRuntimeException_InvalidPassword_Returns500() throws Exception {
    // Given
    when(userService.login(anyString(), anyString()))
        .thenThrow(new RuntimeException("Invalid password"));

    // When & Then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"test@test.com\",\"password\":\"wrong\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal Server Error"))
        .andExpect(jsonPath("$.message").value("Invalid password"));
  }
}

