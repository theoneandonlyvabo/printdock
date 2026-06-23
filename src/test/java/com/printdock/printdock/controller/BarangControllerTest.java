package com.printdock.printdock.controller;

import com.printdock.printdock.dto.request.BarangRequest;
import com.printdock.printdock.dto.response.BarangResponse;
import com.printdock.printdock.exception.BarangNotFoundException;
import com.printdock.printdock.service.BarangService;
import com.printdock.printdock.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BarangControllerTest {

    @Mock
    BarangService barangService;

    @InjectMocks
    BarangController barangController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(barangController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAll_returnsListInApiResponse() throws Exception {
        BarangResponse resp = BarangResponse.builder()
                .id(1L).kodeBarang("BK001").judulBuku("Laskar Pelangi")
                .penerbit("Bentang").hargaSatuan(BigDecimal.valueOf(75000))
                .stok(10).kategoriId(1L).namaKategori("Fiksi").build();
        when(barangService.findAll()).thenReturn(List.of(resp));

        mockMvc.perform(get("/api/barang"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].kodeBarang").value("BK001"));
    }

    @Test
    void getById_notFound_returns404WithErrorResponse() throws Exception {
        when(barangService.findById(99L)).thenThrow(new BarangNotFoundException("Barang tidak ditemukan dengan id: 99"));

        mockMvc.perform(get("/api/barang/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void create_validBody_returns201() throws Exception {
        BarangRequest request = new BarangRequest("BK001", "Laskar Pelangi", "Bentang", BigDecimal.valueOf(75000), 10, 1L);
        BarangResponse resp = BarangResponse.builder().id(1L).kodeBarang("BK001").judulBuku("Laskar Pelangi").stok(10).build();
        when(barangService.create(any(BarangRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/barang").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.kodeBarang").value("BK001"));
    }
}
