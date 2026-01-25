package com.upm.institutional.service;

import com.upm.institutional.model.Sede;
import com.upm.institutional.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SedeService {

    private final SedeRepository sedeRepository;

    public List<Sede> getAllSedes() {
        return sedeRepository.findAll();
    }

    public Sede getSedeById(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede not found"));
    }

    public void saveSede(Sede sede) {
        sedeRepository.save(sede);
    }

    public void deleteSede(Long id) {
        sedeRepository.deleteById(id);
    }
}
