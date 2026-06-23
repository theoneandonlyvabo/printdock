package com.printdock.printdock.repository;

import com.printdock.printdock.model.barang.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {
    Optional<Kategori> findByNamaKategori(String namaKategori);
    boolean existsByNamaKategori(String namaKategori);
}
