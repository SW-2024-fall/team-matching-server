package swe.second.team_matching_server.common.entity;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
@MappedSuperclass
@ToString
@EqualsAndHashCode
@Data
public class Base {
  @Column(updatable = false)
  @CreationTimestamp()
  private LocalDateTime createdAt;

  @UpdateTimestamp()
  private LocalDateTime updatedAt;

  @Column(nullable = true)
  private LocalDateTime deletedAt;

  @Column(nullable = true, columnDefinition = "TEXT")
  private String deletedReason;

  public void delete() {
    this.deletedAt = LocalDateTime.now();
  }

  public void setDeletedReason(String deletedReason) {
    this.deletedReason = deletedReason;
  }
}
