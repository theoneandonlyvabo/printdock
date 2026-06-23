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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarangServiceImpl implements BarangService {

    private final BarangRepository barangRepository;
    private final KategoriRepository kategoriRepository;

    @Override
    public List<BarangResponse> findAll() {
        return barangRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BarangResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    public BarangResponse create(BarangRequest request) {
        validate(request);
        Kategori kategori = kategoriRepository.findById(request.getKategoriId())
                .orElseThrow(() -> new KategoriNotFoundException("Kategori tidak ditemukan dengan id: " + request.getKategoriId()));
        Barang barang = Barang.builder()
                .kodeBarang(request.getKodeBarang())
                .judulBuku(request.getJudulBuku())
                .penerbit(request.getPenerbit())
                .hargaSatuan(request.getHargaSatuan())
                .stok(request.getStok())
                .kategori(kategori)
                .build();
        return toResponse(barangRepository.save(barang));
    }

    @Override
    public BarangResponse update(Long id, BarangRequest request) {
        validate(request);
        Barang barang = getOrThrow(id);
        Kategori kategori = kategoriRepository.findById(request.getKategoriId())
                .orElseThrow(() -> new KategoriNotFoundException("Kategori tidak ditemukan dengan id: " + request.getKategoriId()));
        barang.setKodeBarang(request.getKodeBarang());
        barang.setJudulBuku(request.getJudulBuku());
        barang.setPenerbit(request.getPenerbit());
        barang.setHargaSatuan(request.getHargaSatuan());
        barang.setStok(request.getStok());
        barang.setKategori(kategori);
        return toResponse(barangRepository.save(barang));
    }

    @Override
    public void delete(Long id) {
        getOrThrow(id);
        barangRepository.deleteById(id);
    }

    private void validate(BarangRequest request) {
        if (request.getJudulBuku() == null || request.getJudulBuku().isBlank())
            throw new InvalidInputException("Judul buku tidak boleh kosong");
        if (request.getStok() == null || request.getStok() < 0)
            throw new InvalidInputException("Nilai stok tidak valid");
        if (request.getHargaSatuan() == null || request.getHargaSatuan().signum() < 0)
            throw new InvalidInputException("Harga satuan tidak valid");
    }

    private Barang getOrThrow(Long id) {
        return barangRepository.findById(id)
                .orElseThrow(() -> new BarangNotFoundException("Barang tidak ditemukan dengan id: " + id));
    }

    private BarangResponse toResponse(Barang b) {
        return BarangResponse.builder()
                .id(b.getId())
                .kodeBarang(b.getKodeBarang())
                .judulBuku(b.getJudulBuku())
                .penerbit(b.getPenerbit())
                .hargaSatuan(b.getHargaSatuan())
                .stok(b.getStok())
                .kategoriId(b.getKategori() != null ? b.getKategori().getId() : null)
                .namaKategori(b.getKategori() != null ? b.getKategori().getNamaKategori() : null)
                .build();
    }
}
