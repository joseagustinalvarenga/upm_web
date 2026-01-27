package com.upm.institutional.service;

import com.upm.institutional.model.Feature;
import com.upm.institutional.repository.FeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureService {

    private final FeatureRepository featureRepository;

    public List<Feature> findAllFeatures() {
        return featureRepository.findAllByOrderByDisplayOrderAsc();
    }

    public Feature findById(Long id) {
        return featureRepository.findById(id).orElseThrow(() -> new RuntimeException("Feature no encontrada"));
    }

    @Transactional
    public Feature saveFeature(Feature feature) {
        return featureRepository.save(feature);
    }

    @Transactional
    public void deleteFeature(Long id) {
        featureRepository.deleteById(id);
    }
}
