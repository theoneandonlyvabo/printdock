package com.printdock.printdock.model;

import com.printdock.printdock.model.barang.Barang;
import com.printdock.printdock.model.barang.Kategori;
import com.printdock.printdock.model.transaksi.BarangKeluar;
import com.printdock.printdock.model.transaksi.BarangMasuk;
import com.printdock.printdock.model.transaksi.Transaksi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TransaksiPolymorphismTest {
    private Barang barang;

    @BeforeEach
    void setUp() {
        Kategori kategori = Kategori.builder().id(1L).namaKategori("Fiksi").deskripsi("Buku fiksi").build();
        barang = Barang.builder().id(1L).kodeBarang("BK001").judulBuku("Laskar Pelangi")
                .penerbit("Bentang").hargaSatuan(BigDecimal.valueOf(75000)).stok(10).kategori(kategori).build();
    }

    @Test
    void barangMasuk_proses_incrementsStok() {
        BarangMasuk masuk = new BarangMasuk();
        masuk.setBarang(barang);
        masuk.setJumlah(5);
        masuk.setTanggal(LocalDate.now());
        masuk.proses();
        assertThat(barang.getStok()).isEqualTo(15);
    }

    @Test
    void barangKeluar_proses_decrementsStok() {
        BarangKeluar keluar = new BarangKeluar();
        keluar.setBarang(barang);
        keluar.setJumlah(3);
        keluar.setTanggal(LocalDate.now());
        keluar.proses();
        assertThat(barang.getStok()).isEqualTo(7);
    }

    @Test
    void polymorphicCall_proses_worksForBothSubclasses() {
        BarangMasuk masuk = new BarangMasuk();
        masuk.setBarang(barang);
        masuk.setJumlah(5);
        Transaksi transaksi = masuk;
        transaksi.proses();
        assertThat(barang.getStok()).isEqualTo(15);
    }
}
