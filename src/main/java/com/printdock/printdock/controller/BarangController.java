package com.printdock.printdock.controller;

import com.printdock.printdock.dto.ApiResponse;
import com.printdock.printdock.dto.request.BarangRequest;
import com.printdock.printdock.dto.response.BarangResponse;
import com.printdock.printdock.service.BarangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barang")
@RequiredArgsConstructor
public class BarangController {
    private final BarangService barangService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BarangResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data barang", barangService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BarangResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data barang", barangService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BarangResponse>> create(@RequestBody BarangRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Barang berhasil dibuat", barangService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BarangResponse>> update(@PathVariable Long id, @RequestBody BarangRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Barang berhasil diperbarui", barangService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        barangService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Barang berhasil dihapus", null));
    }
}
