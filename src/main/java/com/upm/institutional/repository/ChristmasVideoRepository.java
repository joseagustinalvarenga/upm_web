package com.upm.institutional.repository;

import com.upm.institutional.model.ChristmasVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChristmasVideoRepository extends JpaRepository<ChristmasVideo, Long> {
    List<ChristmasVideo> findAllByOrderByDisplayOrderAscIdAsc();
    List<ChristmasVideo> findByVisibleTrueOrderByDisplayOrderAscIdAsc();
}
