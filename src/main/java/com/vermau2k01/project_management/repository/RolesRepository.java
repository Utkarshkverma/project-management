package com.vermau2k01.project_management.repository;

import com.vermau2k01.project_management.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Optional<Roles> findByRole(String role);
}
