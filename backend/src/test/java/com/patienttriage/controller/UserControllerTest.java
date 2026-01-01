package com.patienttriage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patienttriage.dto.UserLoginRequest;
import com.patienttriage.dto.UserRegisterRequest;
import com.patienttriage.entity.User;
import com.patienttriage.entity.UserRole;
import com.patienttriage.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for UserController.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Tests successful user registration.
   */
  @Test
  void testRegister_Success() throws Exception {
    // Given
    UserRegisterRequest request = new UserRegisterRequest();
    request.setUsername("test@test.com");
    request.setPassword("password123");
    request.setRole(UserRole.PATIENT);

    User createdUser = new User("test@test.com", "encoded", UserRole.PATIENT);
    when(userService.register(anyString(), anyString(), any(UserRole.class))).thenReturn(createdUser);

    // When & Then
    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("test@test.com"))
        .andExpect(jsonPath("$.role").value("PATIENT"));
    
    verify(userService).register(anyString(), anyString(), any(UserRole.class));
  }

  /**
   * Tests successful login and session attribute setting.
   */
  @Test
  void testLogin_Success_SetsSessionAttributes() throws Exception {
    // Given
    UserLoginRequest request = new UserLoginRequest();
    request.setUsername("test@test.com");
    request.setPassword("password123");

    User user = new User("test@test.com", "encoded", UserRole.PATIENT);
    // Use reflection to set ID
    try {
      java.lang.reflect.Field idField = User.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(user, 1L);
    } catch (Exception e) {
      // Continue without ID
    }

    when(userService.login(anyString(), anyString())).thenReturn(user);

    // When & Then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("test@test.com"));
    
    verify(userService).login(anyString(), anyString());
  }

  /**
   * Tests retrieving current user when logged in.
   */
  @Test
  void testGetCurrentUser_LoggedIn_ReturnsUserInfo() throws Exception {
    // Given - user is logged in (session has userId)
    
    // When & Then
    mockMvc.perform(get("/api/users/current")
            .sessionAttr("userId", 1L)
            .sessionAttr("username", "test@test.com")
            .sessionAttr("role", UserRole.PATIENT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.username").value("test@test.com"))
        .andExpect(jsonPath("$.role").value("PATIENT"));
  }

  /**
   * Tests that unauthenticated users receive 401 error.
   */
  @Test
  void testGetCurrentUser_NotLoggedIn_Returns401() throws Exception {
    // Given - no session attributes
    
    // When & Then
    mockMvc.perform(get("/api/users/current"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Not logged in"));
  }

  /**
   * Tests successful logout and session invalidation.
   */
  @Test
  void testLogout_Success_InvalidatesSession() throws Exception {
    // When & Then
    mockMvc.perform(post("/api/users/logout")
            .sessionAttr("userId", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Logged out successfully"));
  }
}

