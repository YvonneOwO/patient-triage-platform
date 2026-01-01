package com.patienttriage.service;

import com.patienttriage.entity.User;
import com.patienttriage.entity.UserRole;
import com.patienttriage.repository.UserRepository;
import com.patienttriage.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  private User testUser;
  private String testUsername = "testuser@test.com";
  private String testPassword = "password123";
  private String encodedPassword;

  @BeforeEach
  void setUp() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    encodedPassword = encoder.encode(testPassword);
    
    testUser = new User(testUsername, encodedPassword, UserRole.PATIENT);
    // Use reflection to set ID for testing
    try {
      java.lang.reflect.Field idField = User.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testUser, 1L);
    } catch (Exception e) {
      // If reflection fails, continue without ID
    }
  }

  /**
   * Tests successful user registration with password encryption.
   */
  @Test
  void testRegisterUser_Success() {
    // Given
    when(userRepository.existsByUsername(testUsername)).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    User result = userService.register(testUsername, testPassword, UserRole.PATIENT);

    // Then
    assertNotNull(result);
    assertEquals(testUsername, result.getUsername());
    assertEquals(UserRole.PATIENT, result.getRole());
    assertNotEquals(testPassword, result.getPassword()); // Password should be encoded
    verify(userRepository).existsByUsername(testUsername);
    verify(userRepository).save(any(User.class));
  }

  /**
   * Tests that duplicate username registration throws exception.
   */
  @Test
  void testRegisterUser_DuplicateUsername_ThrowsException() {
    // Given
    when(userRepository.existsByUsername(testUsername)).thenReturn(true);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      userService.register(testUsername, testPassword, UserRole.PATIENT);
    });

    assertEquals("Username already exists: " + testUsername, exception.getMessage());
    verify(userRepository).existsByUsername(testUsername);
    verify(userRepository, never()).save(any(User.class));
  }

  /**
   * Tests successful login with valid credentials.
   */
  @Test
  void testLogin_ValidCredentials_Success() {
    // Given
    when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

    // When
    User result = userService.login(testUsername, testPassword);

    // Then
    assertNotNull(result);
    assertEquals(testUsername, result.getUsername());
    verify(userRepository).findByUsername(testUsername);
  }

  /**
   * Tests that login with invalid password throws exception.
   */
  @Test
  void testLogin_InvalidPassword_ThrowsException() {
    // Given
    when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      userService.login(testUsername, "wrongpassword");
    });

    assertEquals("Invalid password", exception.getMessage());
    verify(userRepository).findByUsername(testUsername);
  }

  /**
   * Tests that login with non-existent username throws exception.
   */
  @Test
  void testLogin_UserNotFound_ThrowsException() {
    // Given
    when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      userService.login(testUsername, testPassword);
    });

    assertEquals("User not found", exception.getMessage());
    verify(userRepository).findByUsername(testUsername);
  }
}

