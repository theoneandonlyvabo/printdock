package com.printdock.printdock.model.transaksi;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "barang_keluar")
@Getter
@Setter
@NoArgsConstructor
public class BarangKeluar extends Transaksi {
    @Column(name = "nama_institusi")
    private String namaInstitusi;

    @Column(name = "negara_tujuan")
    private String negaraTujuan;

    @Override
    public void proses() {
        getBarang().setStok(getBarang().getStok() - getJumlah());
    }
}
