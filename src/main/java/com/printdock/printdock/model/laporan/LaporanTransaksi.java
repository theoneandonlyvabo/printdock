package com.printdock.printdock.model.laporan;

import com.printdock.printdock.dto.response.LaporanTransaksiResponse;
import com.printdock.printdock.model.transaksi.BarangKeluar;
import com.printdock.printdock.model.transaksi.BarangMasuk;
import com.printdock.printdock.model.transaksi.JenisTransaksi;
import com.printdock.printdock.model.transaksi.Transaksi;

import java.util.List;
import java.util.stream.Collectors;

public class LaporanTransaksi extends Laporan<LaporanTransaksiResponse> {
    private final List<Transaksi> transaksiList;

    public LaporanTransaksi(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
    }

    @Override
    public List<LaporanTransaksiResponse> generateLaporan() {
        return transaksiList.stream().map(t -> {
            LaporanTransaksiResponse.LaporanTransaksiResponseBuilder b = LaporanTransaksiResponse.builder()
                    .id(t.getId())
                    .tanggal(t.getTanggal())
                    .jumlah(t.getJumlah())
                    .keterangan(t.getKeterangan())
                    .kodeBarang(t.getBarang() != null ? t.getBarang().getKodeBarang() : null)
                    .judulBuku(t.getBarang() != null ? t.getBarang().getJudulBuku() : null);
            if (t instanceof BarangMasuk masuk) {
                b.jenisTransaksi(JenisTransaksi.MASUK).namaPenerbit(masuk.getNamaPenerbit());
            } else if (t instanceof BarangKeluar keluar) {
                b.jenisTransaksi(JenisTransaksi.KELUAR).namaInstitusi(keluar.getNamaInstitusi()).negaraTujuan(keluar.getNegaraTujuan());
            }
            return b.build();
        }).collect(Collectors.toList());
    }
}
