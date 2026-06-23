package com.printdock.printdock.controller;

import com.printdock.printdock.dto.ApiResponse;
import com.printdock.printdock.dto.request.KategoriRequest;
import com.printdock.printdock.dto.response.KategoriResponse;
import com.printdock.printdock.service.KategoriService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kategori")
@RequiredArgsConstructor
public class KategoriController {
    private final KategoriService kategoriService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<KategoriResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data kategori", kategoriService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KategoriResponse>> create(@RequestBody KategoriRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Kategori berhasil dibuat", kategoriService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KategoriResponse>> update(@PathVariable Long id, @RequestBody KategoriRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil diperbarui", kategoriService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        kategoriService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil dihapus", null));
    }
}
