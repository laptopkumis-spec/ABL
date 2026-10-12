package com.aufi.produk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aufi.produk.entity.Jenis;
import com.aufi.produk.repository.JenisRepository;
import java.util.List;

@Service
public class JenisService {

    @Autowired
    private JenisRepository jenisRepository;

    public List<Jenis> getAllJenis() {
        return jenisRepository.findAll();
    }

    public Jenis getJenisById(long id) {
        return jenisRepository.findById(id).orElse(null);
    }

    public Jenis saveJenis(Jenis jenis) {
        return jenisRepository.save(jenis);
    }

    public void deleteJenis(long id) {
        jenisRepository.deleteById(id);
    }
}
