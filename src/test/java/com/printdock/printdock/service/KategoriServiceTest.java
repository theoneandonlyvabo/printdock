package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.KategoriRequest;
import com.printdock.printdock.dto.response.KategoriResponse;
import com.printdock.printdock.exception.KategoriMasihDigunakanException;
import com.printdock.printdock.exception.KategoriNotFoundException;
import com.printdock.printdock.model.barang.Kategori;
import com.printdock.printdock.repository.BarangRepository;
import com.printdock.printdock.repository.KategoriRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KategoriServiceTest {

    @Mock KategoriRepository kategoriRepository;
    @Mock BarangRepository barangRepository;
    @InjectMocks KategoriServiceImpl kategoriService;

    @Test
    void create_validRequest_returnsKategoriResponse() {
        KategoriRequest request = new KategoriRequest("Fiksi", "Buku fiksi");
        Kategori saved = Kategori.builder().id(1L).namaKategori("Fiksi").deskripsi("Buku fiksi").build();
        when(kategoriRepository.save(any(Kategori.class))).thenReturn(saved);

        KategoriResponse response = kategoriService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNamaKategori()).isEqualTo("Fiksi");
    }

    @Test
    void findById_notFound_throwsKategoriNotFoundException() {
        when(kategoriRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kategoriService.findById(99L))
                .isInstanceOf(KategoriNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_kategoriHasBarang_throwsKategoriMasihDigunakanException() {
        Kategori k = Kategori.builder().id(1L).namaKategori("Fiksi").build();
        when(kategoriRepository.findById(1L)).thenReturn(Optional.of(k));
        when(barangRepository.existsByKategoriId(1L)).thenReturn(true);

        assertThatThrownBy(() -> kategoriService.delete(1L))
                .isInstanceOf(KategoriMasihDigunakanException.class);
    }

    @Test
    void delete_kategoriNoBarang_deletesSuccessfully() {
        Kategori k = Kategori.builder().id(1L).namaKategori("Fiksi").build();
        when(kategoriRepository.findById(1L)).thenReturn(Optional.of(k));
        when(barangRepository.existsByKategoriId(1L)).thenReturn(false);

        assertThatCode(() -> kategoriService.delete(1L)).doesNotThrowAnyException();
        verify(kategoriRepository).deleteById(1L);
    }
}
