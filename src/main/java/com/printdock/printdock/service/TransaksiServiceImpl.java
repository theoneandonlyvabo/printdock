package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.BarangKeluarRequest;
import com.printdock.printdock.dto.request.BarangMasukRequest;
import com.printdock.printdock.dto.response.TransaksiResponse;
import com.printdock.printdock.exception.BarangNotFoundException;
import com.printdock.printdock.exception.InvalidInputException;
import com.printdock.printdock.exception.StokTidakCukupException;
import com.printdock.printdock.model.barang.Barang;
import com.printdock.printdock.model.transaksi.BarangKeluar;
import com.printdock.printdock.model.transaksi.BarangMasuk;
import com.printdock.printdock.model.transaksi.JenisTransaksi;
import com.printdock.printdock.model.transaksi.Transaksi;
import com.printdock.printdock.repository.BarangRepository;
import com.printdock.printdock.repository.TransaksiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaksiServiceImpl implements TransaksiService {

    private final TransaksiRepository transaksiRepository;
    private final BarangRepository barangRepository;

    @Override
    @Transactional
    public TransaksiResponse masuk(BarangMasukRequest request) {
        if (request.getJumlah() == null || request.getJumlah() <= 0)
            throw new InvalidInputException("Jumlah transaksi masuk harus lebih dari 0");
        Barang barang = findBarangOrThrow(request.getBarangId());
        BarangMasuk masuk = new BarangMasuk();
        masuk.setBarang(barang);
        masuk.setJumlah(request.getJumlah());
        masuk.setTanggal(request.getTanggal());
        masuk.setKeterangan(request.getKeterangan());
        masuk.setNamaPenerbit(request.getNamaPenerbit());
        masuk.proses();
        barangRepository.save(barang);
        return toResponse((BarangMasuk) transaksiRepository.save(masuk));
    }

    @Override
    @Transactional
    public TransaksiResponse keluar(BarangKeluarRequest request) {
        if (request.getJumlah() == null || request.getJumlah() <= 0)
            throw new InvalidInputException("Jumlah transaksi keluar harus lebih dari 0");
        Barang barang = findBarangOrThrow(request.getBarangId());
        if (barang.getStok() < request.getJumlah())
            throw new StokTidakCukupException("Stok tidak cukup. Stok saat ini: " + barang.getStok() + ", diminta: " + request.getJumlah());
        BarangKeluar keluar = new BarangKeluar();
        keluar.setBarang(barang);
        keluar.setJumlah(request.getJumlah());
        keluar.setTanggal(request.getTanggal());
        keluar.setKeterangan(request.getKeterangan());
        keluar.setNamaInstitusi(request.getNamaInstitusi());
        keluar.setNegaraTujuan(request.getNegaraTujuan());
        keluar.proses();
        barangRepository.save(barang);
        return toResponse((BarangKeluar) transaksiRepository.save(keluar));
    }

    @Override
    public List<TransaksiResponse> findAll() {
        return transaksiRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransaksiResponse findById(Long id) {
        return toResponse(transaksiRepository.findById(id)
                .orElseThrow(() -> new BarangNotFoundException("Transaksi tidak ditemukan dengan id: " + id)));
    }

    private Barang findBarangOrThrow(Long barangId) {
        return barangRepository.findById(barangId)
                .orElseThrow(() -> new BarangNotFoundException("Barang tidak ditemukan dengan id: " + barangId));
    }

    private TransaksiResponse toResponse(Transaksi t) {
        TransaksiResponse.TransaksiResponseBuilder b = TransaksiResponse.builder()
                .id(t.getId())
                .tanggal(t.getTanggal())
                .jumlah(t.getJumlah())
                .keterangan(t.getKeterangan())
                .barangId(t.getBarang() != null ? t.getBarang().getId() : null)
                .judulBuku(t.getBarang() != null ? t.getBarang().getJudulBuku() : null);
        if (t instanceof BarangMasuk masuk) {
            b.jenisTransaksi(JenisTransaksi.MASUK).namaPenerbit(masuk.getNamaPenerbit());
        } else if (t instanceof BarangKeluar keluar) {
            b.jenisTransaksi(JenisTransaksi.KELUAR)
                    .namaInstitusi(keluar.getNamaInstitusi())
                    .negaraTujuan(keluar.getNegaraTujuan());
        }
        return b.build();
    }
}
