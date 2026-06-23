package com.printdock.printdock.model.transaksi;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "barang_masuk")
@Getter
@Setter
@NoArgsConstructor
public class BarangMasuk extends Transaksi {
    @Column(name = "nama_penerbit")
    private String namaPenerbit;

    @Override
    public void proses() {
        getBarang().setStok(getBarang().getStok() + getJumlah());
    }
}
