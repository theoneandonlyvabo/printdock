package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.BarangRequest;
import com.printdock.printdock.dto.response.BarangResponse;
import com.printdock.printdock.exception.BarangNotFoundException;
import com.printdock.printdock.exception.InvalidInputException;
import com.printdock.printdock.exception.KategoriNotFoundException;
import com.printdock.printdock.model.barang.Barang;
import com.printdock.printdock.model.barang.Kategori;
import com.printdock.printdock.repository.BarangRepository;
import com.printdock.printdock.repository.KategoriRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarangServiceTest {

    @Mock BarangRepository barangRepository;
    @Mock KategoriRepository kategoriRepository;
    @InjectMocks BarangServiceImpl barangService;

    private Kategori k() {
        return Kategori.builder().id(1L).namaKategori("Fiksi").deskripsi("d").build();
    }

    private Barang b(Kategori k) {
        return Barang.builder().id(1L).kodeBarang("BK001").judulBuku("Laskar Pelangi")
                .penerbit("Bentang").hargaSatuan(BigDecimal.valueOf(75000)).stok(10).kategori(k).build();
    }

    @Test
    void create_validRequest_returnsBarangResponse() {
        Kategori kat = k();
        BarangRequest req = new BarangRequest("BK001", "Laskar Pelangi", "Bentang", BigDecimal.valueOf(75000), 10, 1L);
        when(kategoriRepository.findById(1L)).thenReturn(Optional.of(kat));
        when(barangRepository.save(any(Barang.class))).thenReturn(b(kat));

        BarangResponse resp = barangService.create(req);

        assertThat(resp.getKodeBarang()).isEqualTo("BK001");
        assertThat(resp.getStok()).isEqualTo(10);
    }

    @Test
    void create_negativeStok_throwsInvalidInputException() {
        BarangRequest req = new BarangRequest("BK001", "Title", "Pub", BigDecimal.valueOf(1000), -1, 1L);

        assertThatThrownBy(() -> barangService.create(req))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("stok");
    }

    @Test
    void create_kategoriNotFound_throwsKategoriNotFoundException() {
        BarangRequest req = new BarangRequest("BK001", "Title", "Pub", BigDecimal.valueOf(1000), 5, 99L);
        when(kategoriRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> barangService.create(req))
                .isInstanceOf(KategoriNotFoundException.class);
    }

    @Test
    void findById_notFound_throwsBarangNotFoundException() {
        when(barangRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> barangService.findById(99L))
                .isInstanceOf(BarangNotFoundException.class);
    }
}
