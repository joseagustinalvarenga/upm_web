package com.upm.institutional.repository;

import com.upm.institutional.model.Course;
import com.upm.institutional.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByStatus(Status status, Pageable pageable);

    @Query("SELECT c.id FROM Course c")
    List<Long> findAllIds();
}
