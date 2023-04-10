package com.example.gateway.repository;


import com.example.gateway.entity.AuthUser;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, String> {
  boolean existsByEmail(String email);
  Optional<AuthUser> findByEmail(String email);

  @Query(
      value =
          "SELECT a.id FROM user_okrs a WHERE a.email = :email",
      nativeQuery = true)
  Optional<String> findIdByEmail(@Param("email") String email);
}
