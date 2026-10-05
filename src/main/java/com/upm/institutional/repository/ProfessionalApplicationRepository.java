package com.upm.institutional.repository;

import com.upm.institutional.model.ApplicationStatus;
import com.upm.institutional.model.ProfessionalApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalApplicationRepository extends JpaRepository<ProfessionalApplication, Long> {
    Page<ProfessionalApplication> findByStatus(ApplicationStatus status, Pageable pageable);
    long countByStatus(ApplicationStatus status);
}
