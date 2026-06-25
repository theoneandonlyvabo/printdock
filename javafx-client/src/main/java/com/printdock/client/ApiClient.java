package com.printdock.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ApiClient {

    private static final String BASE = "http://localhost:8080";
    private static final ObjectMapper JSON = new ObjectMapper();

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public Map<String, Object> login(String username, String password) throws Exception {
        var res = post("/api/auth/login",
                String.format("{\"username\":\"%s\",\"password\":\"%s\"}", esc(username), esc(password)));
        if (res.statusCode() != 200) return null;
        Map<String, Object> env = JSON.readValue(res.body(), new TypeReference<>() {});
        if (!"success".equals(env.get("status"))) return null;
        return cast(env.get("data"));
    }

    public List<Map<String, Object>> getBarang()           throws Exception { return list("/api/barang"); }
    public List<Map<String, Object>> getKategori()         throws Exception { return list("/api/kategori"); }
    public List<Map<String, Object>> getTransaksi()        throws Exception { return list("/api/transaksi"); }
    public List<Map<String, Object>> getLaporanStok()      throws Exception { return list("/api/laporan/stok"); }
    public List<Map<String, Object>> getStokMenipis()      throws Exception { return list("/api/laporan/stok/menipis?threshold=20"); }

    public void createBarang(String json)         throws Exception { post("/api/barang", json); }
    public void updateBarang(long id, String json) throws Exception { send("PUT", "/api/barang/" + id, json); }
    public void deleteBarang(long id)             throws Exception { send("DELETE", "/api/barang/" + id, null); }
    public void createKategori(String json)        throws Exception { post("/api/kategori", json); }
    public void updateKategori(long id, String json) throws Exception { send("PUT", "/api/kategori/" + id, json); }
    public void deleteKategori(long id)           throws Exception { send("DELETE", "/api/kategori/" + id, null); }
    public void transaksiMasuk(String json)       throws Exception { post("/api/transaksi/masuk", json); }
    public void transaksiKeluar(String json)      throws Exception { post("/api/transaksi/keluar", json); }

    // ── internals ─────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> list(String path) throws Exception {
        var env = JSON.readValue(send("GET", path, null).body(), new TypeReference<Map<String, Object>>() {});
        return (List<Map<String, Object>>) env.get("data");
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        return send("POST", path, body);
    }

    private HttpResponse<String> send(String method, String path, String body) throws Exception {
        var b = HttpRequest.newBuilder().uri(URI.create(BASE + path)).header("Content-Type", "application/json");
        switch (method) {
            case "POST"   -> b.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : "{}"));
            case "PUT"    -> b.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : "{}"));
            case "DELETE" -> b.DELETE();
            default       -> b.GET();
        }
        return http.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> cast(Object o) { return (Map<String, Object>) o; }

    private static String esc(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
}
