<div align="center">

# 📦 PrintDock

### Inventory Management System — International Book Distributor

*Built for the warehouses. Designed for the data.*

<br/>

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![OOP](https://img.shields.io/badge/Paradigm-OOP-5C2D91?style=for-the-badge&logo=java&logoColor=white)

![Status](https://img.shields.io/badge/Status-Active_Development-22C55E?style=flat-square)
![Course](https://img.shields.io/badge/Course-Pemrograman_Berorientasi_Objek-3B82F6?style=flat-square)
![University](https://img.shields.io/badge/UPNVJ-S1_Sistem_Informasi-DC2626?style=flat-square)
![Group](https://img.shields.io/badge/Kelompok-5-F59E0B?style=flat-square)

</div>

---

## 🗂️ Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [OOP Concepts](#-oop-concepts-applied)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Tech Stack](#-tech-stack)
- [Team](#-team)

---

## 🌏 Overview

**PrintDock** adalah sistem informasi manajemen inventaris berbasis Java Spring Boot yang dibangun untuk perusahaan distributor buku internasional. Sistem ini mengelola arus buku dari penerbit asal **Amerika Serikat** dan **Inggris** menuju institusi di kawasan **Asia Tenggara** (perpustakaan, universitas, lembaga pendidikan).

> Menyelesaikan masalah penumpukan stok akibat pencatatan manual yang tidak terstruktur, digantikan dengan sistem berbasis data yang dapat ditelusuri dan dilaporkan secara otomatis.

```
Publisher (US / UK) ──▶ [PrintDock Warehouse] ──▶ SEA Institutions
                              ↕
                         Stock Reports
                         Real-time Data
```

---

## ✨ Features

| # | Fitur | Deskripsi | Role |
|---|-------|-----------|------|
| 🔐 | **Login & Auth** | Autentikasi berbasis role (Admin / Staf) dengan validasi kredensial | All |
| 📚 | **Kelola Data Barang** | CRUD buku: kode, judul, penerbit, kategori, harga, stok | Admin |
| 🏷️ | **Kelola Kategori** | Manajemen genre dan klasifikasi buku (Business, Tech, dll) | Admin |
| 📥 | **Barang Masuk** | Pencatatan penerimaan buku dari penerbit, stok auto-update | Admin / Staf |
| 📤 | **Barang Keluar** | Pencatatan distribusi ke institusi SEA, validasi stok real-time | Admin / Staf |
| 📊 | **Laporan Stok** | Ringkasan stok terkini, riwayat transaksi, alert stok menipis | Admin |

---

## 🧱 OOP Concepts Applied

### 🔷 Inheritance

```
User (parent)
├── Admin       → full access: kelola data, laporan, kategori
└── Staff       → limited: catat transaksi masuk/keluar

Transaksi (parent)
├── BarangMasuk → tambah stok dari penerbit
└── BarangKeluar → kurang stok ke institusi tujuan
```

### 🔶 Encapsulation

```java
// Semua atribut domain dideklarasikan private
public class Barang {
    private String kodeBarang;
    private String judulBuku;
    private String penerbit;
    private int stok;

    // Akses hanya via getter/setter
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
}
```

### 🔷 Polymorphism

```java
// Method overriding pada subkelas laporan
Laporan laporan = new LaporanStok();
laporan.generateLaporan(); // → output stok per buku

laporan = new LaporanTransaksi();
laporan.generateLaporan(); // → output riwayat transaksi

// Runtime polymorphism pada Transaksi
Transaksi t = new BarangMasuk(...);
t.proses(); // → tambah stok

t = new BarangKeluar(...);
t.proses(); // → kurang stok + validasi
```

### 🔴 Exception Handling

```java
try {
    transaksiService.prosesBarangKeluar(kodeBarang, jumlah);
} catch (StokTidakCukupException e) {
    // Pengeluaran tidak dapat melebihi stok tersedia
    return ResponseEntity.badRequest().body(e.getMessage());
} catch (BarangNotFoundException e) {
    // Barang tidak ditemukan dalam sistem
    return ResponseEntity.notFound().build();
} catch (InvalidInputException e) {
    // Input stok bernilai negatif atau tidak valid
    return ResponseEntity.badRequest().body(e.getMessage());
}
```

---

## 📁 Project Structure

```
printdock/
├── 📂 src/
│   └── 📂 main/
│       ├── 📂 java/com/printdock/printdock/
│       │   ├── 📂 controller/        # REST Controllers
│       │   ├── 📂 service/           # Business Logic
│       │   ├── 📂 repository/        # Data Access Layer
│       │   ├── 📂 model/             # Domain Entities
│       │   │   ├── 📂 user/          # User, Admin, Staff
│       │   │   ├── 📂 barang/        # Barang, Kategori
│       │   │   ├── 📂 transaksi/     # Transaksi, BarangMasuk, BarangKeluar
│       │   │   └── 📂 laporan/       # Laporan, LaporanStok
│       │   ├── 📂 exception/         # Custom Exceptions
│       │   └── 📂 config/            # Security & App Config
│       └── 📂 resources/
│           └── application.properties
├── 📂 build/
├── 📄 build.gradle
├── 📄 settings.gradle
└── 📄 README.md
```

---

## 🚀 Getting Started

### Prerequisites

```bash
☑️  Java 21+
☑️  Gradle 8.x
☑️  PostgreSQL / MySQL (atau H2 untuk dev)
```

### Clone & Run

```bash
# Clone repository
git clone https://github.com/theoneandonlyvabo/printdock.git
cd printdock

# Build project
./gradlew build

# Run application
./gradlew bootRun
```

### Configuration

```properties
# src/main/resources/application.properties

spring.application.name=printdock
spring.datasource.url=jdbc:postgresql://localhost:5432/printdock_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

---

## 🔌 API Endpoints

```
BASE URL: http://localhost:8080/api
```

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| `POST` | `/auth/login` | Login pengguna | All |
| `GET` | `/barang` | List semua barang | All |
| `POST` | `/barang` | Tambah barang baru | Admin |
| `PUT` | `/barang/{id}` | Update data barang | Admin |
| `DELETE` | `/barang/{id}` | Hapus barang | Admin |
| `GET` | `/kategori` | List semua kategori | All |
| `POST` | `/kategori` | Tambah kategori | Admin |
| `POST` | `/transaksi/masuk` | Catat barang masuk | Admin / Staf |
| `POST` | `/transaksi/keluar` | Catat barang keluar | Admin / Staf |
| `GET` | `/laporan/stok` | Generate laporan stok | Admin |
| `GET` | `/laporan/transaksi` | Riwayat transaksi | Admin |

---

## 🛠️ Tech Stack

```yaml
Language:     Java 21
Framework:    Spring Boot 3.x
Build Tool:   Gradle
Database:     PostgreSQL (prod) / H2 (dev)
ORM:          Spring Data JPA / Hibernate
Security:     Spring Security
Architecture: Layered (Controller → Service → Repository)
Paradigm:     Object-Oriented Programming (OOP)
```

---

## 👥 Team

> Kelompok 5 — Sistem Informasi Manajemen Inventaris Barang
> Pemrograman Berorientasi Objek · S1 Sistem Informasi · UPNVJ · 2026

| Nama | NIM | Peran |
|------|-----|-------|
| Dara Ramadhani Aresti | 2410512109 | — |
| **Airel Adrivano** | 2410512135 | Full-stack Engineer |
| Anggota 3 | 2410512— | — |
| Anggota 4 | 2410512— | — |

---

<div align="center">

**PrintDock** · `com.printdock` · Built with ☕ Java & 🌿 Spring Boot

![Made with Java](https://img.shields.io/badge/Made_with-Java-ED8B00?style=flat-square&logo=openjdk)
![Spring](https://img.shields.io/badge/Powered_by-Spring_Boot-6DB33F?style=flat-square&logo=springboot)
![UPNVJ](https://img.shields.io/badge/UPNVJ-2026-DC2626?style=flat-square)

</div>