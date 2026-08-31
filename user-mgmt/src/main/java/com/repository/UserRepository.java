package com.repository;

import com.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDetail, Long> {

    boolean existsByEmail(String email);

    Optional<UserDetail> findByEmail(String email);
}
