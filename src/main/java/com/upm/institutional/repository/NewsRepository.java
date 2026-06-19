package com.upm.institutional.repository;

import com.upm.institutional.model.News;
import com.upm.institutional.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findByStatus(Status status, Pageable pageable);

    @Query("SELECT n.id FROM News n")
    List<Long> findAllIds();
}
