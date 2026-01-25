package com.upm.institutional.repository;

import com.upm.institutional.model.Professional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalRepository
        extends JpaRepository<Professional, Long>, JpaSpecificationExecutor<Professional> {
    Page<Professional> findByProfessionContainingIgnoreCaseOrNameContainingIgnoreCase(String profession, String name,
            Pageable pageable);
}
