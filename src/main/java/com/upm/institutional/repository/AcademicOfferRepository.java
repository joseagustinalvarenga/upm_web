package com.upm.institutional.repository;

import com.upm.institutional.model.AcademicOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicOfferRepository
        extends JpaRepository<AcademicOffer, Long>, JpaSpecificationExecutor<AcademicOffer> {

    List<AcademicOffer> findByAreaContainingIgnoreCase(String area);

    List<AcademicOffer> findByCourseContainingIgnoreCase(String course);

    // For search functionality across multiple fields
    List<AcademicOffer> findByAreaContainingIgnoreCaseOrCourseContainingIgnoreCase(String area, String course);

    // To get all distinct areas for the filter dropdown
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT a.area FROM AcademicOffer a ORDER BY a.area")
    List<String> findDistinctAreas();
}
