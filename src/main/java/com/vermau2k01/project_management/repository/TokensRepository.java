package com.vermau2k01.project_management.repository;

import com.vermau2k01.project_management.entity.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokensRepository extends JpaRepository<Tokens, Integer> {

    Optional<Tokens> findByToken(String token);
}
