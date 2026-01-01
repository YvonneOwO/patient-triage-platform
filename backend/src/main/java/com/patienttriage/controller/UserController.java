package com.patienttriage.controller;


import com.patienttriage.dto.UserLoginRequest;
import com.patienttriage.dto.UserRegisterRequest;
import com.patienttriage.entity.User;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.patienttriage.service.UserService;

/**
 * REST controller for user management operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }
  // TODO: need a get API for register, so that the web will not automatically go to the login page.

  /**
   * Registers a new user.
   * 
   * @param request the registration request containing username, password, and role
   * @return the newly created User entity
   */
  @PostMapping("/register")
  public User register(@RequestBody UserRegisterRequest request) {
    return userService.register(
        request.getUsername(),
        request.getPassword(),
        request.getRole()
    );
  }

  /**
   * Authenticates a user and stores session information.
   * 
   * @param request the login request containing username and password
   * @param session HTTP session to store user information
   * @return HTTP 200 OK with User entity, or error response
   */
  @PostMapping("/login")
  public ResponseEntity<User> login(@RequestBody UserLoginRequest request, HttpSession session) {
    User user = userService.login(
        request.getUsername(),
        request.getPassword()
    );
    
    // Store user ID in session
    session.setAttribute("userId", user.getId());
    session.setAttribute("username", user.getUsername());
    session.setAttribute("role", user.getRole());
    
    return ResponseEntity.ok(user);
  }

  /**
   * Retrieves the current logged-in user from session.
   * 
   * @param session HTTP session containing user information
   * @return HTTP 200 OK with user information, or 401 if not logged in
   */
  @GetMapping("/current")
  public ResponseEntity<Object> getCurrentUser(HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
      return ResponseEntity.status(401).body(Map.of("message", "Not logged in"));
    }
    return ResponseEntity.ok(Map.of(
        "userId", userId,
        "username", session.getAttribute("username"),
        "role", session.getAttribute("role")
    ));
  }

  /**
   * Logs out the current user by invalidating the session.
   * 
   * @param session HTTP session to invalidate
   * @return HTTP 200 OK with success message
   */
  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(HttpSession session) {
    session.invalidate();
    return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
  }

  // TODO: If there's new user, direct to the register link


}