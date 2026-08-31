package com.repository;

import com.enums.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository  {
    Optional<Permissions> findByName();
}
