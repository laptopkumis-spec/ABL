package com.aufi.pelanggan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aufi.pelanggan.entity.Pelanggan;
import com.aufi.pelanggan.repository.PelangganRepository;
import java.util.List;

@Service
public class PelangganService {

    @Autowired
    private PelangganRepository pelangganRepository;

    public List<Pelanggan> getAllPelanggan() {
        return pelangganRepository.findAll();
    }

    public Pelanggan getPelangganById(long id) {
        return pelangganRepository.findById(id).orElse(null);
    }

    public Pelanggan savePelanggan(Pelanggan pelanggan) {
        return pelangganRepository.save(pelanggan);
    }

    public void deletePelanggan(long id) {
        pelangganRepository.deleteById(id);
    }

    public Pelanggan updatePelanggan(Long id, Pelanggan pelanggan) {
        Pelanggan existing = pelangganRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setNama(pelanggan.getNama());
            existing.setAlamat(pelanggan.getAlamat());
            existing.setJenisKelamin(pelanggan.getJenisKelamin());
            return pelangganRepository.save(existing);
        }
        return null;
    }
}
