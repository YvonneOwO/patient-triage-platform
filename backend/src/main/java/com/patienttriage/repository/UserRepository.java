package com.patienttriage.repository;

import com.patienttriage.entity.User;
import com.patienttriage.entity.UserRole;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user by username.
   * 
   * @param username the username to search for
   * @return Optional containing the User if found
   */
  Optional<User> findByUsername(String username);

  /**
   * Checks whether a username already exists.
   * 
   * @param username the username to check
   * @return true if username exists, false otherwise
   */
  boolean existsByUsername(String username);



}


