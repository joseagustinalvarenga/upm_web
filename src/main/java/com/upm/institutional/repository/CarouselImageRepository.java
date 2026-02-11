package com.upm.institutional.repository;

import com.upm.institutional.model.CarouselImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarouselImageRepository extends JpaRepository<CarouselImage, Long> {
}
