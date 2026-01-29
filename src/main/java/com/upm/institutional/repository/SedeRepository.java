package com.upm.institutional.repository;

import com.upm.institutional.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    Optional<Sede> findByName(String name);
}
