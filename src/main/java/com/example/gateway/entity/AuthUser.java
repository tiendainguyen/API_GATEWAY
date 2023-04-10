package com.example.gateway.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user_okrs")
public class AuthUser {
  @Id
  private String id;
  private String name;
  private String phone;
  private String email;
  private Integer dateOfBirth;
  private String gender;
  private String address;
  private String avatar;
  public static AuthUser from(String name, String email) {
    var user = new AuthUser();
    user.setName(name);
    user.setEmail(email);
    return user;
  }
}
