package com.printdock.printdock.config;

import com.printdock.printdock.model.barang.Barang;
import com.printdock.printdock.model.barang.Kategori;
import com.printdock.printdock.model.user.Role;
import com.printdock.printdock.model.user.Staff;
import com.printdock.printdock.repository.BarangRepository;
import com.printdock.printdock.repository.KategoriRepository;
import com.printdock.printdock.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final KategoriRepository kategoriRepository;
    private final BarangRepository barangRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {
        seedUsers();
        seedKategoriAndBarang();
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("vano")) {
            Staff vano = new Staff();
            vano.setUsername("vano");
            vano.setPassword(passwordEncoder.encode("12345678"));
            vano.setRole(Role.STAFF);
            userRepository.save(vano);
        }
    }

    private void seedKategoriAndBarang() {
        if (kategoriRepository.count() > 0) return;

        Kategori business = kategoriRepository.save(
                Kategori.builder().namaKategori("Business").deskripsi("Buku bisnis dan manajemen").build());
        Kategori technology = kategoriRepository.save(
                Kategori.builder().namaKategori("Technology").deskripsi("Buku teknologi dan pemrograman").build());
        Kategori selfDev = kategoriRepository.save(
                Kategori.builder().namaKategori("Self-Development").deskripsi("Buku pengembangan diri").build());

        List<Barang> books = List.of(
                Barang.builder().kodeBarang("BK-001").judulBuku("The Lean Startup").penerbit("Crown Business")
                        .hargaSatuan(new BigDecimal("285000")).stok(45).kategori(business).build(),
                Barang.builder().kodeBarang("BK-002").judulBuku("Zero to One").penerbit("Crown Business")
                        .hargaSatuan(new BigDecimal("260000")).stok(12).kategori(business).build(),
                Barang.builder().kodeBarang("BK-003").judulBuku("Clean Code").penerbit("Pearson")
                        .hargaSatuan(new BigDecimal("320000")).stok(30).kategori(technology).build(),
                Barang.builder().kodeBarang("BK-004").judulBuku("The Pragmatic Programmer").penerbit("Pearson")
                        .hargaSatuan(new BigDecimal("350000")).stok(8).kategori(technology).build(),
                Barang.builder().kodeBarang("BK-005").judulBuku("Atomic Habits").penerbit("Penguin Random House")
                        .hargaSatuan(new BigDecimal("210000")).stok(60).kategori(selfDev).build(),
                Barang.builder().kodeBarang("BK-006").judulBuku("Deep Work").penerbit("Grand Central Publishing")
                        .hargaSatuan(new BigDecimal("195000")).stok(15).kategori(selfDev).build()
        );
        barangRepository.saveAll(books);
    }
}
