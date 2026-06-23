package com.printdock.printdock.controller;

import com.printdock.printdock.dto.ApiResponse;
import com.printdock.printdock.dto.response.LaporanStokResponse;
import com.printdock.printdock.dto.response.LaporanTransaksiResponse;
import com.printdock.printdock.service.LaporanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laporan")
@RequiredArgsConstructor
public class LaporanController {
    private final LaporanService laporanService;

    @GetMapping("/stok")
    public ResponseEntity<ApiResponse<List<LaporanStokResponse>>> getLaporanStok() {
        return ResponseEntity.ok(ApiResponse.success("Laporan stok berhasil dibuat", laporanService.getLaporanStok()));
    }

    @GetMapping("/transaksi")
    public ResponseEntity<ApiResponse<List<LaporanTransaksiResponse>>> getLaporanTransaksi() {
        return ResponseEntity.ok(ApiResponse.success("Laporan transaksi berhasil dibuat", laporanService.getLaporanTransaksi()));
    }

    @GetMapping("/stok/menipis")
    public ResponseEntity<ApiResponse<List<LaporanStokResponse>>> getLaporanStokMenipis(
            @RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(ApiResponse.success("Laporan stok menipis berhasil dibuat", laporanService.getLaporanStokMenipis(threshold)));
    }
}
