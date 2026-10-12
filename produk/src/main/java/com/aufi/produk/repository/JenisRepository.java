package com.aufi.produk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aufi.produk.entity.Jenis;

@Repository
public interface JenisRepository extends JpaRepository<Jenis, Long> {
}
