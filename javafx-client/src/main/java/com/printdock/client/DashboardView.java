package com.printdock.client;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.text.NumberFormat;
import java.util.*;

public class DashboardView extends BorderPane {

    private static final String SIDEBAR_BG  = "#0D2244";
    private static final String ACTIVE_BG   = "#1E3A6E";
    private static final String PRIMARY     = "#004dbc";
    private static final String CONTENT_BG  = "#f0f3ff";
    private static final String CARD_BG     = "white";
    private static final String TEXT_MAIN   = "#1a1c2e";
    private static final String TEXT_MUTED  = "#6B7280";

    private final ApiClient api = new ApiClient();
    private final String username;
    private final StackPane contentArea = new StackPane();
    private final Map<String, Button> navButtons = new LinkedHashMap<>();

    public DashboardView(String username, String role) {
        this.username = username;
        setLeft(buildSidebar());
        contentArea.setStyle("-fx-background-color: " + CONTENT_BG + ";");
        setCenter(contentArea);
        showPanel("Dashboard");
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + ";");

        // Logo
        HBox logo = new HBox(10);
        logo.setPadding(new Insets(20, 16, 20, 16));
        logo.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("📦");
        icon.setStyle("-fx-font-size: 20px;");
        Label name = new Label("PrintDock");
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: white;");
        logo.getChildren().addAll(icon, name);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

        // Nav items
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 8, 8, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);

        for (String[] item : new String[][]{
                {"📊", "Dashboard"},
                {"📚", "Data Barang"},
                {"🏷️", "Kategori"},
                {"📥", "Barang Masuk"},
                {"📤", "Barang Keluar"},
                {"📋", "Laporan Stok"}
        }) {
            Button btn = navButton(item[0], item[1]);
            navButtons.put(item[1], btn);
            nav.getChildren().add(btn);
        }

        // User info at bottom
        HBox user = new HBox(10);
        user.setPadding(new Insets(16));
        user.setAlignment(Pos.CENTER_LEFT);
        user.setStyle("-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1 0 0 0;");
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 18px;");
        VBox userInfo = new VBox(2);
        Label uname = new Label(username);
        uname.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600;");
        Label logoutLink = new Label("Keluar");
        logoutLink.setStyle("-fx-text-fill: #b2c5ff; -fx-font-size: 10px; -fx-cursor: hand;");
        logoutLink.setOnMouseClicked(e -> App.showLogin());
        userInfo.getChildren().addAll(uname, logoutLink);
        user.getChildren().addAll(avatar, userInfo);

        sidebar.getChildren().addAll(logo, sep, nav, user);
        return sidebar;
    }

    private Button navButton(String emoji, String label) {
        Button btn = new Button(emoji + "  " + label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 12, 10, 12));
        btn.setStyle(navStyle(false));
        btn.setOnAction(e -> showPanel(label));
        return btn;
    }

    private void showPanel(String name) {
        navButtons.forEach((k, b) -> b.setStyle(navStyle(k.equals(name))));
        Node panel = switch (name) {
            case "Data Barang"  -> buildDataBarangPanel();
            case "Kategori"     -> buildKategoriPanel();
            case "Barang Masuk" -> buildTransaksiPanel(true);
            case "Barang Keluar"-> buildTransaksiPanel(false);
            case "Laporan Stok" -> buildLaporanPanel();
            default             -> buildOverviewPanel();
        };
        contentArea.getChildren().setAll(panel);
    }

    // ── Overview ──────────────────────────────────────────────────────────────

    private Node buildOverviewPanel() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + CONTENT_BG + "; -fx-background: " + CONTENT_BG + ";");

        VBox root = new VBox(16);
        root.setPadding(new Insets(24));

        Label title = pageTitle("Overview Dashboard");

        HBox statRow = new HBox(16);
        Label[] statLabels = {new Label("—"), new Label("—"), new Label("—"), new Label("—")};
        statRow.getChildren().addAll(
                statCard("Total Stok", statLabels[0], "#004dbc"),
                statCard("Barang Masuk", statLabels[1], "#059669"),
                statCard("Barang Keluar", statLabels[2], "#DC2626"),
                statCard("Kategori Aktif", statLabels[3], "#7C3AED")
        );
        for (Node c : statRow.getChildren()) HBox.setHgrow(c, Priority.ALWAYS);

        // Recent transactions table
        Label transaksiTitle = sectionTitle("Transaksi Terbaru");
        TableView<Map<String, Object>> txTable = txTableView();
        txTable.setPrefHeight(240);

        // Low stock panel
        Label stokTitle = sectionTitle("Stok Menipis");
        VBox stokList = new VBox(8);
        stokList.setPadding(new Insets(12));
        stokList.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        root.getChildren().addAll(title, statRow, transaksiTitle, card(txTable), stokTitle, stokList);
        scroll.setContent(root);

        // Load data async
        new Thread(() -> {
            try {
                var barang = api.getBarang();
                var transaksi = api.getTransaksi();
                var kategori = api.getKategori();
                var menipis = api.getStokMenipis();

                int totalStok = barang.stream().mapToInt(b -> toInt(b.get("stok"))).sum();
                long masukCount = transaksi.stream().filter(t -> "MASUK".equals(t.get("jenisTransaksi"))).count();
                long keluarCount = transaksi.stream().filter(t -> "KELUAR".equals(t.get("jenisTransaksi"))).count();

                // Most recent 5
                List<Map<String, Object>> recent = transaksi.size() > 5
                        ? transaksi.subList(transaksi.size() - 5, transaksi.size()) : transaksi;
                Collections.reverse(new ArrayList<>(recent));

                Platform.runLater(() -> {
                    statLabels[0].setText(String.valueOf(totalStok));
                    statLabels[1].setText(String.valueOf(masukCount));
                    statLabels[2].setText(String.valueOf(keluarCount));
                    statLabels[3].setText(String.valueOf(kategori.size()));
                    txTable.setItems(FXCollections.observableArrayList(recent));

                    stokList.getChildren().clear();
                    if (menipis.isEmpty()) {
                        stokList.getChildren().add(new Label("Semua stok aman ✓"));
                    } else {
                        for (var b : menipis) {
                            HBox row = new HBox(12);
                            row.setAlignment(Pos.CENTER_LEFT);
                            Label bname = new Label(str(b.get("judulBuku")));
                            bname.setStyle("-fx-font-size: 12px; -fx-font-weight: 600;");
                            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                            Label stok = new Label(b.get("stok") + " unit");
                            stok.setStyle("-fx-font-size: 11px; -fx-text-fill: #DC2626; -fx-font-weight: 700;");
                            row.getChildren().addAll(bname, sp, stok);
                            stokList.getChildren().add(row);
                        }
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> statLabels[0].setText("Error"));
            }
        }).start();

        return scroll;
    }

    private TableView<Map<String, Object>> txTableView() {
        TableView<Map<String, Object>> t = new TableView<>();
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.getColumns().addAll(
                col("Judul Buku", "judulBuku", 220),
                col("Jumlah", "jumlah", 80),
                col("Tipe", "jenisTransaksi", 90),
                col("Tanggal", "tanggal", 110)
        );
        return t;
    }

    // ── Data Barang ───────────────────────────────────────────────────────────

    private Node buildDataBarangPanel() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(pageTitle("Data Barang"));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button addBtn = primaryBtn("+ Tambah Buku");
        header.getChildren().add(addBtn);

        TableView<Map<String, Object>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Map<String, Object>, String> aksiCol = new TableColumn<>("Aksi");
        aksiCol.setPrefWidth(80);
        aksiCol.setCellFactory(c -> new TableCell<>() {
            final Button del = new Button("🗑");
            final Button edit = new Button("✏");
            final HBox box = new HBox(6, edit, del);
            {
                box.setAlignment(Pos.CENTER);
                del.setStyle("-fx-background-color: #fee2e2; -fx-cursor: hand; -fx-background-radius: 6;");
                edit.setStyle("-fx-background-color: #dbeafe; -fx-cursor: hand; -fx-background-radius: 6;");
                del.setOnAction(e -> {
                    var item = getTableView().getItems().get(getIndex());
                    deleteBarang(item, getTableView());
                });
                edit.setOnAction(e -> {
                    var item = getTableView().getItems().get(getIndex());
                    showEditBarangDialog(item, getTableView());
                });
            }
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(
                col("Kode", "kodeBarang", 90),
                col("Judul Buku", "judulBuku", 200),
                col("Penerbit", "penerbit", 130),
                col("Kategori", "namaKategori", 110),
                colMoney("Harga", "hargaSatuan", 120),
                col("Stok", "stok", 70),
                aksiCol
        );

        addBtn.setOnAction(e -> showAddBarangDialog(table));
        loadBarang(table);

        root.getChildren().addAll(header, card(table));
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + CONTENT_BG + "; -fx-background: " + CONTENT_BG + ";");
        return scroll;
    }

    private void loadBarang(TableView<Map<String, Object>> table) {
        new Thread(() -> {
            try {
                var data = api.getBarang();
                Platform.runLater(() -> table.setItems(FXCollections.observableArrayList(data)));
            } catch (Exception ex) { showAlert("Gagal memuat data barang"); }
        }).start();
    }

    private void showAddBarangDialog(TableView<Map<String, Object>> table) {
        Dialog<String> d = new Dialog<>();
        d.setTitle("Tambah Buku");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = formGrid();
        TextField kode     = new TextField(); kode.setPromptText("cth: BK-001");
        TextField judul    = new TextField(); judul.setPromptText("cth: Clean Code");
        TextField penerbit = new TextField(); penerbit.setPromptText("cth: Pearson");
        TextField harga    = new TextField(); harga.setPromptText("cth: 150000");
        TextField stok     = new TextField(); stok.setPromptText("cth: 50");
        ComboBox<String> katCombo = new ComboBox<>();
        katCombo.setPromptText("Pilih Kategori");
        katCombo.setMaxWidth(Double.MAX_VALUE);
        katCombo.setStyle("-fx-background-color: #F5F9FE; -fx-border-color: #E1EBF5; -fx-border-radius: 6; -fx-background-radius: 6;");

        // map display name → id for submission
        Map<String, Long> katMap = new LinkedHashMap<>();
        try {
            for (var k : api.getKategori()) {
                String label = str(k.get("namaKategori"));
                katMap.put(label, toLong(k.get("id")));
                katCombo.getItems().add(label);
            }
        } catch (Exception ignored) {}

        addFormRow(grid, 0, "Kode Barang", kode);
        addFormRow(grid, 1, "Judul Buku",  judul);
        addFormRow(grid, 2, "Penerbit",    penerbit);
        addFormRow(grid, 3, "Harga Satuan", harga);
        addFormRow(grid, 4, "Stok",        stok);
        Label katLabel = new Label("Kategori:");
        katLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        grid.add(katLabel, 0, 5);
        grid.add(katCombo, 1, 5);
        d.getDialogPane().setContent(grid);

        d.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            Long katId = katMap.get(katCombo.getValue());
            return String.format(
                    "{\"kodeBarang\":\"%s\",\"judulBuku\":\"%s\",\"penerbit\":\"%s\"," +
                    "\"hargaSatuan\":%s,\"stok\":%s,\"kategoriId\":%s}",
                    kode.getText(), judul.getText(), penerbit.getText(),
                    harga.getText().isEmpty() ? "0" : harga.getText(),
                    stok.getText().isEmpty()  ? "0" : stok.getText(),
                    katId != null ? katId : "null");
        });

        d.showAndWait().ifPresent(json -> new Thread(() -> {
            try { api.createBarang(json); Platform.runLater(() -> loadBarang(table)); }
            catch (Exception ex) { showAlert("Gagal menambah buku"); }
        }).start());
    }

    private void showEditBarangDialog(Map<String, Object> item, TableView<Map<String, Object>> table) {
        Dialog<String> d = new Dialog<>();
        d.setTitle("Edit Buku");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = formGrid();
        TextField judul    = new TextField(str(item.get("judulBuku")));
        TextField penerbit = new TextField(str(item.get("penerbit")));
        TextField harga    = new TextField(str(item.get("hargaSatuan")));
        TextField stok     = new TextField(str(item.get("stok")));
        ComboBox<String> katCombo = new ComboBox<>();
        katCombo.setMaxWidth(Double.MAX_VALUE);
        katCombo.setStyle("-fx-background-color: #F5F9FE; -fx-border-color: #E1EBF5; -fx-border-radius: 6; -fx-background-radius: 6;");

        Map<String, Long> katMap = new LinkedHashMap<>();
        try {
            for (var k : api.getKategori()) {
                String label = str(k.get("namaKategori"));
                katMap.put(label, toLong(k.get("id")));
                katCombo.getItems().add(label);
            }
            // pre-select current category
            long currentKatId = toLong(item.get("kategoriId"));
            katMap.forEach((name, id2) -> { if (id2 == currentKatId) katCombo.setValue(name); });
        } catch (Exception ignored) {}

        addFormRow(grid, 0, "Judul Buku",   judul);
        addFormRow(grid, 1, "Penerbit",     penerbit);
        addFormRow(grid, 2, "Harga Satuan", harga);
        addFormRow(grid, 3, "Stok",         stok);
        Label katLabel = new Label("Kategori:");
        katLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        grid.add(katLabel, 0, 4);
        grid.add(katCombo, 1, 4);
        d.getDialogPane().setContent(grid);

        long id   = toLong(item.get("id"));
        String kode = str(item.get("kodeBarang"));

        d.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            Long katId = katMap.get(katCombo.getValue());
            return String.format(
                    "{\"kodeBarang\":\"%s\",\"judulBuku\":\"%s\",\"penerbit\":\"%s\"," +
                    "\"hargaSatuan\":%s,\"stok\":%s,\"kategoriId\":%s}",
                    kode, judul.getText(), penerbit.getText(),
                    harga.getText(), stok.getText(), katId != null ? katId : "null");
        });

        d.showAndWait().ifPresent(json -> new Thread(() -> {
            try { api.updateBarang(id, json); Platform.runLater(() -> loadBarang(table)); }
            catch (Exception ex) { showAlert("Gagal mengupdate buku"); }
        }).start());
    }

    private void deleteBarang(Map<String, Object> item, TableView<Map<String, Object>> table) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus \"" + item.get("judulBuku") + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b ->
                new Thread(() -> {
                    try { api.deleteBarang(toLong(item.get("id"))); Platform.runLater(() -> loadBarang(table)); }
                    catch (Exception ex) { showAlert("Gagal menghapus buku"); }
                }).start());
    }

    // ── Kategori ──────────────────────────────────────────────────────────────

    private Node buildKategoriPanel() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(pageTitle("Manajemen Kategori"));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button addBtn = primaryBtn("+ Tambah Kategori");
        header.getChildren().add(addBtn);

        FlowPane grid = new FlowPane(16, 16);
        grid.setPadding(new Insets(4));

        addBtn.setOnAction(e -> {
            Dialog<String> d = new Dialog<>();
            d.setTitle("Tambah Kategori");
            d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            GridPane form = formGrid();
            TextField nama = new TextField(); TextField desk = new TextField();
            addFormRow(form, 0, "Nama Kategori", nama);
            addFormRow(form, 1, "Deskripsi", desk);
            d.getDialogPane().setContent(form);
            d.setResultConverter(b -> b == ButtonType.OK
                    ? String.format("{\"namaKategori\":\"%s\",\"deskripsi\":\"%s\"}", nama.getText(), desk.getText()) : null);
            d.showAndWait().ifPresent(json -> new Thread(() -> {
                try { api.createKategori(json); Platform.runLater(() -> loadKategori(grid)); }
                catch (Exception ex) { showAlert("Gagal menambah kategori"); }
            }).start());
        });

        loadKategori(grid);

        root.getChildren().addAll(header, grid);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + CONTENT_BG + "; -fx-background: " + CONTENT_BG + ";");
        return scroll;
    }

    private void loadKategori(FlowPane grid) {
        new Thread(() -> {
            try {
                var data = api.getKategori();
                Platform.runLater(() -> {
                    grid.getChildren().clear();
                    for (var k : data) grid.getChildren().add(kategoriCard(k, grid));
                });
            } catch (Exception ex) { showAlert("Gagal memuat kategori"); }
        }).start();
    }

    private Node kategoriCard(Map<String, Object> k, FlowPane grid) {
        VBox card = new VBox(8);
        card.setPrefWidth(180);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Label nama = new Label(str(k.get("namaKategori")));
        nama.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_MAIN + ";");
        nama.setWrapText(true);

        Label desk = new Label(str(k.get("deskripsi")));
        desk.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + ";");
        desk.setWrapText(true);

        Button edit = new Button("Edit");
        edit.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8; -fx-font-size: 10px; -fx-cursor: hand; -fx-background-radius: 6;");
        edit.setOnAction(e -> showEditKategoriDialog(k, grid));

        Button del = new Button("Hapus");
        del.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #DC2626; -fx-font-size: 10px; -fx-cursor: hand; -fx-background-radius: 6;");
        del.setOnAction(e -> new Thread(() -> {
            try { api.deleteKategori(toLong(k.get("id"))); Platform.runLater(() -> loadKategori(grid)); }
            catch (Exception ex) { showAlert("Gagal menghapus kategori"); }
        }).start());

        HBox actions = new HBox(6, edit, del);
        card.getChildren().addAll(nama, desk, actions);
        return card;
    }

    private void showEditKategoriDialog(Map<String, Object> k, FlowPane grid) {
        Dialog<String> d = new Dialog<>();
        d.setTitle("Edit Kategori");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane form = formGrid();
        TextField nama = new TextField(str(k.get("namaKategori")));
        TextField desk = new TextField(str(k.get("deskripsi")));
        addFormRow(form, 0, "Nama Kategori", nama);
        addFormRow(form, 1, "Deskripsi", desk);
        d.getDialogPane().setContent(form);
        d.setResultConverter(b -> b == ButtonType.OK
                ? String.format("{\"namaKategori\":\"%s\",\"deskripsi\":\"%s\"}", nama.getText(), desk.getText()) : null);
        d.showAndWait().ifPresent(json -> new Thread(() -> {
            try { api.updateKategori(toLong(k.get("id")), json); Platform.runLater(() -> loadKategori(grid)); }
            catch (Exception ex) { showAlert("Gagal mengupdate kategori"); }
        }).start());
    }

    // ── Transaksi ─────────────────────────────────────────────────────────────

    private Node buildTransaksiPanel(boolean masuk) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.getChildren().add(pageTitle(masuk ? "Input Barang Masuk" : "Input Barang Keluar"));

        GridPane form = formGrid();
        TextField barangId = new TextField(); barangId.setPromptText("ID Barang");
        TextField jumlah   = new TextField(); jumlah.setPromptText("Jumlah");
        TextField tanggal  = new TextField(); tanggal.setPromptText("yyyy-MM-dd");
        TextField ket      = new TextField(); ket.setPromptText("Keterangan");
        TextField extra1   = new TextField();
        TextField extra2   = new TextField();

        addFormRow(form, 0, "Barang ID", barangId);
        addFormRow(form, 1, "Jumlah", jumlah);
        addFormRow(form, 2, "Tanggal", tanggal);
        addFormRow(form, 3, "Keterangan", ket);
        if (masuk) {
            extra1.setPromptText("Nama Penerbit");
            addFormRow(form, 4, "Nama Penerbit", extra1);
        } else {
            extra1.setPromptText("Nama Institusi");
            extra2.setPromptText("Negara Tujuan");
            addFormRow(form, 4, "Nama Institusi", extra1);
            addFormRow(form, 5, "Negara Tujuan", extra2);
        }

        Label status = new Label();
        Button submit = primaryBtn(masuk ? "Catat Transaksi Masuk" : "Catat Transaksi Keluar");
        submit.setOnAction(e -> {
            String json = masuk
                    ? String.format("{\"barangId\":%s,\"jumlah\":%s,\"tanggal\":\"%s\",\"keterangan\":\"%s\",\"namaPenerbit\":\"%s\"}",
                    barangId.getText(), jumlah.getText(), tanggal.getText(), ket.getText(), extra1.getText())
                    : String.format("{\"barangId\":%s,\"jumlah\":%s,\"tanggal\":\"%s\",\"keterangan\":\"%s\",\"namaInstitusi\":\"%s\",\"negaraTujuan\":\"%s\"}",
                    barangId.getText(), jumlah.getText(), tanggal.getText(), ket.getText(), extra1.getText(), extra2.getText());
            new Thread(() -> {
                try {
                    if (masuk) api.transaksiMasuk(json); else api.transaksiKeluar(json);
                    Platform.runLater(() -> {
                        status.setStyle("-fx-text-fill: #059669;");
                        status.setText("✓ Transaksi berhasil dicatat");
                        barangId.clear(); jumlah.clear(); tanggal.clear(); ket.clear(); extra1.clear(); extra2.clear();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> { status.setStyle("-fx-text-fill: #DC2626;"); status.setText("✗ Gagal: " + ex.getMessage()); });
                }
            }).start();
        });

        root.getChildren().addAll(card(form), submit, status);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + CONTENT_BG + "; -fx-background: " + CONTENT_BG + ";");
        return scroll;
    }

    // ── Laporan Stok ──────────────────────────────────────────────────────────

    private Node buildLaporanPanel() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.getChildren().add(pageTitle("Laporan Stok"));

        // Summary stat cards
        Label totalStokVal   = new Label("—");
        Label totalMasukVal  = new Label("—");
        Label totalKeluarVal = new Label("—");
        Label stokMenipisVal = new Label("—");
        HBox statRow = new HBox(16,
                statCard("Total Stok", totalStokVal, "#004dbc"),
                statCard("Total Masuk", totalMasukVal, "#059669"),
                statCard("Total Keluar", totalKeluarVal, "#DC2626"),
                statCard("Stok Menipis", stokMenipisVal, "#D97706"));
        for (Node c : statRow.getChildren()) HBox.setHgrow(c, Priority.ALWAYS);

        // Main stock table
        TableView<Map<String, Object>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(360);
        table.getColumns().addAll(
                col("Kode", "kodeBarang", 90),
                col("Judul Buku", "judulBuku", 220),
                col("Penerbit", "penerbit", 130),
                col("Kategori", "namaKategori", 110),
                colMoney("Harga", "hargaSatuan", 120),
                col("Stok", "stok", 70)
        );

        // Low-stock section
        Label stokMenipisTitle = sectionTitle("Peringatan Stok Menipis (≤ 20 unit)");
        VBox stokMenipisList = new VBox(8);
        stokMenipisList.setPadding(new Insets(12));
        stokMenipisList.setStyle("-fx-background-color: #fff7ed; -fx-background-radius: 8;");

        new Thread(() -> {
            try {
                var data    = api.getLaporanStok();
                var tx      = api.getTransaksi();
                var menipis = api.getStokMenipis();

                int totalStok   = data.stream().mapToInt(b -> toInt(b.get("stok"))).sum();
                long totalMasuk = tx.stream().filter(t -> "MASUK".equals(t.get("jenisTransaksi")))
                        .mapToLong(t -> toLong(t.get("jumlah"))).sum();
                long totalKeluar = tx.stream().filter(t -> "KELUAR".equals(t.get("jenisTransaksi")))
                        .mapToLong(t -> toLong(t.get("jumlah"))).sum();

                Platform.runLater(() -> {
                    table.setItems(FXCollections.observableArrayList(data));
                    totalStokVal.setText(String.valueOf(totalStok));
                    totalMasukVal.setText(String.valueOf(totalMasuk));
                    totalKeluarVal.setText(String.valueOf(totalKeluar));
                    stokMenipisVal.setText(String.valueOf(menipis.size()));

                    stokMenipisList.getChildren().clear();
                    if (menipis.isEmpty()) {
                        stokMenipisList.getChildren().add(new Label("✓ Semua stok aman"));
                    } else {
                        for (var b : menipis) {
                            HBox row = new HBox(12);
                            row.setAlignment(Pos.CENTER_LEFT);
                            Label bname = new Label(str(b.get("judulBuku")));
                            bname.setStyle("-fx-font-size: 12px; -fx-font-weight: 600;");
                            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                            Label stok = new Label(b.get("stok") + " unit");
                            stok.setStyle("-fx-font-size: 11px; -fx-text-fill: #DC2626; -fx-font-weight: 700;");
                            row.getChildren().addAll(bname, sp, stok);
                            stokMenipisList.getChildren().add(row);
                        }
                    }
                });
            } catch (Exception ex) { showAlert("Gagal memuat laporan stok"); }
        }).start();

        root.getChildren().addAll(statRow, sectionTitle("Stok Per Buku"), card(table),
                stokMenipisTitle, stokMenipisList);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + CONTENT_BG + "; -fx-background: " + CONTENT_BG + ";");
        return scroll;
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private Node statCard(String label, Label valueLabel, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");

        Label lbl = new Label(label.toUpperCase());
        lbl.setStyle("-fx-font-size: 9px; -fx-font-weight: 600; -fx-text-fill: " + TEXT_MUTED + ";");
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(lbl, valueLabel);
        return card;
    }

    private Node card(Node content) {
        VBox c = new VBox(content);
        c.setPadding(new Insets(16));
        c.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");
        return c;
    }

    private Label pageTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_MAIN + ";");
        return l;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: " + TEXT_MAIN + ";");
        return l;
    }

    private Button primaryBtn(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; " +
                "-fx-font-weight: 600; -fx-font-size: 12px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16;");
        return b;
    }

    private String navStyle(boolean active) {
        String bg = active ? ACTIVE_BG : "transparent";
        return "-fx-background-color: " + bg + "; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-alignment: CENTER_LEFT; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 12;";
    }

    private GridPane formGrid() {
        GridPane g = new GridPane();
        g.setHgap(12); g.setVgap(10);
        g.setPadding(new Insets(8));
        ColumnConstraints c1 = new ColumnConstraints(110);
        ColumnConstraints c2 = new ColumnConstraints(240);
        g.getColumnConstraints().addAll(c1, c2);
        return g;
    }

    private void addFormRow(GridPane g, int row, String label, TextField field) {
        Label l = new Label(label + ":");
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        field.setStyle("-fx-background-color: #F5F9FE; -fx-border-color: #E1EBF5; " +
                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10;");
        field.setMaxWidth(Double.MAX_VALUE);
        g.add(l, 0, row);
        g.add(field, 1, row);
    }

    @SuppressWarnings("unchecked")
    private <S> TableColumn<S, String> col(String title, String key, double w) {
        TableColumn<S, String> c = new TableColumn<>(title);
        c.setPrefWidth(w);
        c.setCellValueFactory(d -> {
            Object v = ((Map<String, Object>) d.getValue()).get(key);
            return new SimpleStringProperty(v != null ? v.toString() : "");
        });
        return c;
    }

    @SuppressWarnings("unchecked")
    private <S> TableColumn<S, String> colMoney(String title, String key, double w) {
        TableColumn<S, String> c = new TableColumn<>(title);
        c.setPrefWidth(w);
        c.setCellValueFactory(d -> {
            Object v = ((Map<String, Object>) d.getValue()).get(key);
            if (v == null) return new SimpleStringProperty("");
            try {
                long val = Double.valueOf(v.toString()).longValue();
                return new SimpleStringProperty("Rp " + String.format("%,d", val).replace(',', '.'));
            } catch (Exception e) { return new SimpleStringProperty(v.toString()); }
        });
        return c;
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
    }

    private static String str(Object o) { return o != null ? o.toString() : ""; }
    private static int toInt(Object o) {
        if (o == null) return 0;
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return 0; }
    }
    private static long toLong(Object o) {
        if (o == null) return 0;
        try { return Long.parseLong(o.toString()); } catch (Exception e) { return 0; }
    }
}
