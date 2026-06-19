package com.upm.institutional.repository;

import com.upm.institutional.model.CarouselImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface CarouselImageRepository extends JpaRepository<CarouselImage, Long> {
    java.util.List<CarouselImage> findByLocation(String location);

    @Query("SELECT ci.id FROM CarouselImage ci")
    List<Long> findAllIds();
}
