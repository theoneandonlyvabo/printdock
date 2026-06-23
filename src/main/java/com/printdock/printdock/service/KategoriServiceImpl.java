package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.KategoriRequest;
import com.printdock.printdock.dto.response.KategoriResponse;
import com.printdock.printdock.exception.InvalidInputException;
import com.printdock.printdock.exception.KategoriMasihDigunakanException;
import com.printdock.printdock.exception.KategoriNotFoundException;
import com.printdock.printdock.model.barang.Kategori;
import com.printdock.printdock.repository.BarangRepository;
import com.printdock.printdock.repository.KategoriRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KategoriServiceImpl implements KategoriService {

    private final KategoriRepository kategoriRepository;
    private final BarangRepository barangRepository;

    @Override
    public List<KategoriResponse> findAll() {
        return kategoriRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public KategoriResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    public KategoriResponse create(KategoriRequest request) {
        if (request.getNamaKategori() == null || request.getNamaKategori().isBlank())
            throw new InvalidInputException("Nama kategori tidak boleh kosong");
        Kategori k = Kategori.builder()
                .namaKategori(request.getNamaKategori())
                .deskripsi(request.getDeskripsi())
                .build();
        return toResponse(kategoriRepository.save(k));
    }

    @Override
    public KategoriResponse update(Long id, KategoriRequest request) {
        if (request.getNamaKategori() == null || request.getNamaKategori().isBlank())
            throw new InvalidInputException("Nama kategori tidak boleh kosong");
        Kategori k = getOrThrow(id);
        k.setNamaKategori(request.getNamaKategori());
        k.setDeskripsi(request.getDeskripsi());
        return toResponse(kategoriRepository.save(k));
    }

    @Override
    public void delete(Long id) {
        getOrThrow(id);
        if (barangRepository.existsByKategoriId(id))
            throw new KategoriMasihDigunakanException("Kategori dengan id " + id + " masih digunakan oleh barang");
        kategoriRepository.deleteById(id);
    }

    private Kategori getOrThrow(Long id) {
        return kategoriRepository.findById(id)
                .orElseThrow(() -> new KategoriNotFoundException("Kategori tidak ditemukan dengan id: " + id));
    }

    private KategoriResponse toResponse(Kategori k) {
        return KategoriResponse.builder()
                .id(k.getId())
                .namaKategori(k.getNamaKategori())
                .deskripsi(k.getDeskripsi())
                .build();
    }
}
