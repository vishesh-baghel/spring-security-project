package com.example.springsecurity.client.repository;

import com.example.springsecurity.client.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
     static PasswordResetToken findByToken(String token) {
          return null;
     }
}
