package com.sa.event_mng.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sa.event_mng.model.entity.User;



public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndEnabledTrue(String username);

    Optional<User> findByEmailAndEnabledTrue(String email);

    Optional<User> findByIdAndEnabledTrue(Long id);

    List<User> findAllByEnabledTrue();

    Optional<User> findByVerificationToken(String verificationToken);

    boolean existsByUsername(String username);

    boolean existsByEmailAndEnabledTrue(String email);
}
