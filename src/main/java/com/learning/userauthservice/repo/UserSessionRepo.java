package com.learning.userauthservice.repo;

import com.learning.userauthservice.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepo extends JpaRepository<UserSession,Long> {
    Optional<UserSession> findByTokenAndUserId(String token, Long user_id);
}
