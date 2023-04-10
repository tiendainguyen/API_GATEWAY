package com.example.gateway.entity;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor(staticName = "of")
@Entity
@Data
@NoArgsConstructor
@Table(name = "account")
//@EntityListeners(AuditingEntityListener.class)
public class AuthAccount  {
  private String username;
  private String password;
  private Boolean isActivated = false;
  private Boolean isLockPermanent = false;
  private String userId;
  @Id
  private String id;
  @PrePersist
  public void ensureId() {
    this.id = Objects.isNull(this.id) ? UUID.randomUUID().toString() : this.id;
  }
  public static AuthAccount of(String userId, String username, String password) {
    return new AuthAccount(username, password, false, false, userId);
  }

  public AuthAccount(String username, String password, Boolean isActivated, Boolean isLockPermanent,
      String userId) {
    this.username = username;
    this.password = password;
    this.isActivated = isActivated;
    this.isLockPermanent = isLockPermanent;
    this.userId = userId;
  }
}
