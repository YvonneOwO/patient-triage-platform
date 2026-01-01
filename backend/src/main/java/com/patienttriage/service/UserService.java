package com.patienttriage.service;

import com.patienttriage.entity.User;
import com.patienttriage.entity.UserRole;

/**
 * Service interface for user management operations.
 */
public interface UserService {
  /**
   * Registers a new user with encrypted password.
   * 
   * @param username the username for the new user
   * @param rawPassword the plain text password to be encrypted
   * @param role the role of the user (ADMIN, DOCTOR, PATIENT)
   * @return the newly created User entity
   */
  User register(String username, String rawPassword, UserRole role);

  /**
   * Authenticates a user and verifies password.
   * 
   * @param username the username to authenticate
   * @param rawPassword the plain text password to verify
   * @return the authenticated User entity
   */
  User login(String username, String rawPassword);

  /**
   * Finds a user by username.
   * 
   * @param username the username to search for
   * @return the User entity with the matching username
   */
  User findByUsername(String username);
}
