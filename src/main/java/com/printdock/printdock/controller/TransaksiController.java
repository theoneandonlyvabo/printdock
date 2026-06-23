package com.printdock.printdock.controller;

import com.printdock.printdock.dto.ApiResponse;
import com.printdock.printdock.dto.request.BarangKeluarRequest;
import com.printdock.printdock.dto.request.BarangMasukRequest;
import com.printdock.printdock.dto.response.TransaksiResponse;
import com.printdock.printdock.service.TransaksiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaksi")
@RequiredArgsConstructor
public class TransaksiController {
    private final TransaksiService transaksiService;

    @PostMapping("/masuk")
    public ResponseEntity<ApiResponse<TransaksiResponse>> masuk(@RequestBody BarangMasukRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaksi masuk berhasil", transaksiService.masuk(request)));
    }

    @PostMapping("/keluar")
    public ResponseEntity<ApiResponse<TransaksiResponse>> keluar(@RequestBody BarangKeluarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaksi keluar berhasil", transaksiService.keluar(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransaksiResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data transaksi", transaksiService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransaksiResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data transaksi", transaksiService.findById(id)));
    }
}
