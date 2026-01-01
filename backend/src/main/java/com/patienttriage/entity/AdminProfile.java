package com.patienttriage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entity representing an admin profile.
 */
@Entity
@Table(name = "admin_profile")
public class AdminProfile {

  @Id                                                                                                                                                            
  private Long adminId; // getter only

  @MapsId
  @OneToOne
  @JoinColumn(name = "admin_id")
  private User admin; // getter only

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "audit_logs")
  private String auditLogs;

  @Column(name = "permissions")
  private String permissions;


  public Long getAdminId() {
    return adminId;
  }

  public User getAdmin() {
    return admin;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getAuditLogs() {
    return auditLogs;
  }

  public String getPermissions() {
    return permissions;
  }

  public void setPermissions(String permissions) {
    this.permissions = permissions;
  }
}
