package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.BarangKeluarRequest;
import com.printdock.printdock.dto.request.BarangMasukRequest;
import com.printdock.printdock.dto.response.TransaksiResponse;
import com.printdock.printdock.exception.BarangNotFoundException;
import com.printdock.printdock.exception.InvalidInputException;
import com.printdock.printdock.exception.StokTidakCukupException;
import com.printdock.printdock.model.barang.Barang;
import com.printdock.printdock.model.barang.Kategori;
import com.printdock.printdock.model.transaksi.BarangKeluar;
import com.printdock.printdock.model.transaksi.BarangMasuk;
import com.printdock.printdock.repository.BarangRepository;
import com.printdock.printdock.repository.TransaksiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransaksiServiceTest {

    @Mock TransaksiRepository transaksiRepository;
    @Mock BarangRepository barangRepository;
    @InjectMocks TransaksiServiceImpl transaksiService;

    private Barang barang;

    @BeforeEach
    void setUp() {
        Kategori k = Kategori.builder().id(1L).namaKategori("Fiksi").build();
        barang = Barang.builder().id(1L).kodeBarang("BK001").judulBuku("Laskar Pelangi")
                .penerbit("Bentang").hargaSatuan(BigDecimal.valueOf(75000)).stok(10).kategori(k).build();
    }

    @Test
    void masuk_validRequest_incrementsStokAndSaves() {
        BarangMasukRequest req = new BarangMasukRequest(1L, LocalDate.now(), 5, "restock", "Bentang Pustaka");
        BarangMasuk saved = new BarangMasuk();
        saved.setId(1L);
        saved.setBarang(barang);
        saved.setJumlah(5);
        saved.setTanggal(LocalDate.now());
        saved.setNamaPenerbit("Bentang Pustaka");
        when(barangRepository.findById(1L)).thenReturn(Optional.of(barang));
        when(transaksiRepository.save(any(BarangMasuk.class))).thenReturn(saved);

        TransaksiResponse resp = transaksiService.masuk(req);

        assertThat(resp.getNamaPenerbit()).isEqualTo("Bentang Pustaka");
        assertThat(barang.getStok()).isEqualTo(15);
        verify(barangRepository).save(barang);
    }

    @Test
    void keluar_sufficientStok_decrementsStokAndSaves() {
        BarangKeluarRequest req = new BarangKeluarRequest(1L, LocalDate.now(), 3, "order", "Univ A", "Malaysia");
        BarangKeluar saved = new BarangKeluar();
        saved.setId(1L);
        saved.setBarang(barang);
        saved.setJumlah(3);
        when(barangRepository.findById(1L)).thenReturn(Optional.of(barang));
        when(transaksiRepository.save(any(BarangKeluar.class))).thenReturn(saved);

        transaksiService.keluar(req);

        assertThat(barang.getStok()).isEqualTo(7);
        verify(barangRepository).save(barang);
    }

    @Test
    void keluar_insufficientStok_throwsStokTidakCukupException() {
        BarangKeluarRequest req = new BarangKeluarRequest(1L, LocalDate.now(), 20, "order", "Univ A", "Malaysia");
        when(barangRepository.findById(1L)).thenReturn(Optional.of(barang));

        assertThatThrownBy(() -> transaksiService.keluar(req))
                .isInstanceOf(StokTidakCukupException.class)
                .hasMessageContaining("Stok");
    }

    @Test
    void masuk_negativeJumlah_throwsInvalidInputException() {
        BarangMasukRequest req = new BarangMasukRequest(1L, LocalDate.now(), -5, "bad", "Bentang");

        assertThatThrownBy(() -> transaksiService.masuk(req))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void masuk_barangNotFound_throwsBarangNotFoundException() {
        BarangMasukRequest req = new BarangMasukRequest(99L, LocalDate.now(), 5, "restock", "Bentang");
        when(barangRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transaksiService.masuk(req))
                .isInstanceOf(BarangNotFoundException.class);
    }
}
