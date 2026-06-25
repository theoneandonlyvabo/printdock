package com.printdock.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

/**
 * JavaFX port of frontend/view.html — split-layout login screen.
 * Left: dark branding panel (#0D2244). Right: login form.
 */
public class LoginView {

    private static final String COLOR_BRAND_BG   = "#0D2244";
    private static final String COLOR_PRIMARY     = "#004dbc";
    private static final String COLOR_SURFACE     = "#f9f9ff";
    private static final String COLOR_INPUT_BG    = "#F5F9FE";
    private static final String COLOR_INPUT_BORDER = "#E1EBF5";
    private static final String COLOR_TEXT        = "#1a1c2e";
    private static final String COLOR_MUTED       = "#6B7280";

    private final HBox root;

    private final TextField     usernameField  = new TextField();
    private final PasswordField passwordField  = new PasswordField();
    private final TextField     passwordPlain  = new TextField();
    private final Label         errorLabel     = new Label();
    private final Button        loginButton    = new Button("Masuk ke Dashboard");

    private final ApiClient api = new ApiClient();

    public LoginView() {
        root = new HBox();
        root.setPrefSize(1000, 620);

        VBox branding = buildBranding();
        VBox form     = buildForm();

        HBox.setHgrow(branding, Priority.ALWAYS);
        HBox.setHgrow(form, Priority.ALWAYS);
        branding.setMaxWidth(Double.MAX_VALUE);
        form.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(branding, form);
    }

    // ── Left panel ────────────────────────────────────────────────────────────

    private VBox buildBranding() {
        VBox pane = new VBox(24);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(60));
        pane.setStyle("-fx-background-color: " + COLOR_BRAND_BG + ";");

        // Logo row
        HBox logo = new HBox(10);
        logo.setAlignment(Pos.CENTER);

        Label iconBox = new Label("📦");
        iconBox.setStyle("-fx-font-size: 26px; -fx-background-color: #3B5BAD; " +
                "-fx-background-radius: 10; -fx-padding: 6 8;");

        Label appName = new Label("PrintDock");
        appName.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: white;");

        logo.getChildren().addAll(iconBox, appName);

        // Tagline
        Label tagline = new Label("Kelola inventaris toko buku Anda\ndengan mudah dan efisien");
        tagline.setStyle("-fx-font-size: 13px; -fx-text-fill: #b2c5ff; -fx-line-spacing: 2;");
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setAlignment(Pos.CENTER);

        // Feature badges
        VBox badges = new VBox(10);
        badges.setMaxWidth(300);
        for (String[] b : new String[][]{
                {"📊", "Real-time Stock Monitoring"},
                {"🚀", "Fast & Efficient Management"},
                {"🔒", "Secure Distribution System"}
        }) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 16, 10, 16));
            row.setMaxWidth(Double.MAX_VALUE);
            row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 8;");

            Label ico = new Label(b[0]);
            ico.setStyle("-fx-font-size: 14px;");

            Label txt = new Label(b[1]);
            txt.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

            row.getChildren().addAll(ico, txt);
            badges.getChildren().add(row);
        }

        Label footer = new Label("International Logistics Standard Compliance");
        footer.setStyle("-fx-text-fill: rgba(255,255,255,0.3); -fx-font-size: 9px;");

        pane.getChildren().addAll(logo, tagline, badges, footer);
        return pane;
    }

    // ── Right panel ───────────────────────────────────────────────────────────

    private VBox buildForm() {
        VBox outer = new VBox();
        outer.setAlignment(Pos.CENTER);
        outer.setPadding(new Insets(60));
        outer.setStyle("-fx-background-color: " + COLOR_SURFACE + ";");

        VBox inner = new VBox(16);
        inner.setMaxWidth(360);

        // Heading
        Label heading = new Label("Selamat Datang Kembali");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: " + COLOR_TEXT + ";");

        Label sub = new Label("Masukkan kredensial Anda untuk mengakses dashboard distribusi.");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + COLOR_MUTED + ";");
        sub.setWrapText(true);

        // Username
        Label usernameLabel = new Label("USERNAME");
        usernameLabel.setStyle(labelCss());
        usernameField.setPromptText("Email atau ID Karyawan");
        usernameField.setStyle(inputCss());
        usernameField.setPrefHeight(40);

        VBox usernameGroup = new VBox(5, usernameLabel, usernameField);

        // Password
        HBox pwLabelRow = new HBox();
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setStyle(labelCss());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Hyperlink forgot = new Hyperlink("Lupa Password?");
        forgot.setStyle("-fx-font-size: 9px; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-border-color: transparent;");
        pwLabelRow.getChildren().addAll(passwordLabel, spacer, forgot);

        // Password field + plain-text overlay toggled by eye button
        passwordField.setPromptText("••••••••");
        passwordField.setStyle(transparentFieldCss());
        passwordField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(passwordField, Priority.ALWAYS);

        passwordPlain.setPromptText("••••••••");
        passwordPlain.setStyle(transparentFieldCss());
        passwordPlain.setVisible(false);
        passwordPlain.setManaged(false);
        passwordPlain.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(passwordPlain, Priority.ALWAYS);

        // Sync both fields bidirectionally so getText() on either returns the same value
        passwordField.textProperty().bindBidirectional(passwordPlain.textProperty());

        Button eyeBtn = new Button("👁");
        eyeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 8;");
        eyeBtn.setOnAction(e -> togglePassword());

        // Outer container provides the visible border
        HBox pwContainer = new HBox(4, passwordField, passwordPlain, eyeBtn);
        pwContainer.setAlignment(Pos.CENTER_LEFT);
        pwContainer.setPrefHeight(40);
        pwContainer.setPadding(new Insets(0, 0, 0, 10));
        pwContainer.setStyle("-fx-background-color: " + COLOR_INPUT_BG + "; " +
                "-fx-border-color: " + COLOR_INPUT_BORDER + "; " +
                "-fx-border-width: 0.5; -fx-border-radius: 8; -fx-background-radius: 8;");

        VBox passwordGroup = new VBox(5, pwLabelRow, pwContainer);

        // Error message (hidden initially)
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; " +
                "-fx-font-size: 11px; -fx-padding: 10 14; -fx-background-radius: 8;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Submit button
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(42);
        loginButton.setStyle(buttonCss());
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> handleLogin());

        // Enter key on all input fields
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
        passwordPlain.setOnAction(e -> handleLogin());

        // Footer
        Separator sep = new Separator();
        VBox.setMargin(sep, new Insets(4, 0, 4, 0));
        Label help = new Label("Butuh bantuan akses? Hubungi Administrator IT");
        help.setStyle("-fx-font-size: 10px; -fx-text-fill: " + COLOR_MUTED + ";");
        help.setMaxWidth(Double.MAX_VALUE);
        help.setAlignment(Pos.CENTER);

        inner.getChildren().addAll(heading, sub, usernameGroup, passwordGroup,
                errorLabel, loginButton, sep, help);
        outer.getChildren().add(inner);
        return outer;
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    private void togglePassword() {
        boolean show = !passwordPlain.isVisible();
        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
        passwordPlain.setVisible(show);
        passwordPlain.setManaged(show);
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("⚠  Mohon isi username dan password.");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Memproses...");
        hideError();

        new Thread(() -> {
            try {
                var userData = api.login(user, pass);
                Platform.runLater(() -> {
                    resetButton();
                    if (userData != null) {
                        onSuccess(user);
                    } else {
                        showError("⚠  Username atau password yang Anda masukkan salah.");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    resetButton();
                    showError("⚠  Tidak dapat terhubung ke server (localhost:8080).");
                });
            }
        }).start();
    }

    private void onSuccess(String username) {
        App.showDashboard(username, "STAFF");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void resetButton() {
        loginButton.setDisable(false);
        loginButton.setText("Masuk ke Dashboard");
    }

    // ── CSS helpers ───────────────────────────────────────────────────────────

    private String inputCss() {
        return "-fx-background-color: " + COLOR_INPUT_BG + "; " +
                "-fx-border-color: " + COLOR_INPUT_BORDER + "; " +
                "-fx-border-width: 0.5; -fx-border-radius: 8; -fx-background-radius: 8; " +
                "-fx-font-size: 12px; -fx-padding: 0 10;";
    }

    /** Used for fields inside the password container — no visible border of their own. */
    private String transparentFieldCss() {
        return "-fx-background-color: transparent; " +
                "-fx-background-insets: 0; -fx-background-radius: 0; " +
                "-fx-border-color: transparent; -fx-font-size: 12px;";
    }

    private String labelCss() {
        return "-fx-font-size: 9px; -fx-font-weight: 600; -fx-text-fill: #9CA3AF; -fx-letter-spacing: 0.5;";
    }

    private String buttonCss() {
        return "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; " +
                "-fx-font-weight: 700; -fx-font-size: 13px; " +
                "-fx-background-radius: 8; -fx-cursor: hand;";
    }

    public HBox getRoot() {
        return root;
    }
}
