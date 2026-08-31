package com.repository;

import com.entity.Roles;
import com.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

    @Query(value = "SELECT r FROM Roles r WHERE r.roles = :roles")
    Optional<Roles> findByRoles(@Param("roles") String roles);
}
