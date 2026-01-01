package com.patienttriage.service.impl;

import com.patienttriage.repository.UserRepository;
import com.patienttriage.entity.User;
import com.patienttriage.entity.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.patienttriage.service.UserService;

/**
 * Implementation of UserService for user management operations.
 */
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Registers a new user with encrypted password.
   * 
   * @param username the username for the new user
   * @param rawPassword the plain text password to be encrypted
   * @param role the role of the user (ADMIN, DOCTOR, PATIENT)
   * @return the newly created User entity
   */
  @Override
  public User register(String username, String rawPassword, UserRole role) {

    // Check duplicates
    if (userRepository.existsByUsername(username)) {
      throw new RuntimeException("Username already exists: " + username);
    }

    // Encrypt password
    String encodedPassword = passwordEncoder.encode(rawPassword);

    // Create user entity
    User newUser = new User(username, encodedPassword, role);

    // Save to database
    return userRepository.save(newUser);
  }

  /**
   * Authenticates a user and verifies password.
   * 
   * @param username the username to authenticate
   * @param rawPassword the plain text password to verify
   * @return the authenticated User entity
   */
  @Override
  public User login(String username, String rawPassword) {

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Validate password
    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new RuntimeException("Invalid password");
    }

    return user;
  }

  /**
   * Finds a user by username.
   * 
   * @param username the username to search for
   * @return the User entity with the matching username
   */
  @Override
  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

}