package com.printdock.printdock.model.laporan;

import com.printdock.printdock.dto.response.LaporanStokResponse;
import com.printdock.printdock.model.barang.Barang;

import java.util.List;
import java.util.stream.Collectors;

public class LaporanStok extends Laporan<LaporanStokResponse> {
    private final List<Barang> barangList;

    public LaporanStok(List<Barang> barangList) {
        this.barangList = barangList;
    }

    @Override
    public List<LaporanStokResponse> generateLaporan() {
        return barangList.stream()
                .map(b -> LaporanStokResponse.builder()
                        .id(b.getId())
                        .kodeBarang(b.getKodeBarang())
                        .judulBuku(b.getJudulBuku())
                        .penerbit(b.getPenerbit())
                        .stok(b.getStok())
                        .hargaSatuan(b.getHargaSatuan())
                        .namaKategori(b.getKategori() != null ? b.getKategori().getNamaKategori() : null)
                        .build())
                .collect(Collectors.toList());
    }
}
