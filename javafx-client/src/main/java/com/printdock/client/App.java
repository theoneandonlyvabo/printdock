package com.printdock.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.Taskbar;

public class App extends Application {

    private static Stage stage;

    static final Image APP_ICON = new Image(
            App.class.getResourceAsStream("/com/printdock/client/logo.png"));

    @Override
    public void start(Stage s) {
        stage = s;
        stage.setTitle("PrintDock");
        stage.getIcons().add(APP_ICON);
        setDockIcon();
        stage.setScene(new Scene(new LoginView().getRoot(), 1000, 620));
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.show();
    }

    private static void setDockIcon() {
        if (!Taskbar.isTaskbarSupported()) return;
        try {
            var tb = Taskbar.getTaskbar();
            if (tb.isSupported(Taskbar.Feature.ICON_IMAGE))
                tb.setIconImage(SwingFXUtils.fromFXImage(APP_ICON, null));
        } catch (Exception ignored) {}
    }

    public static void showDashboard(String username, String role) {
        Platform.runLater(() -> {
            stage.getScene().setRoot(new DashboardView(username, role));
            stage.setWidth(1280);
            stage.setHeight(800);
            stage.centerOnScreen();
        });
    }

    public static void showLogin() {
        Platform.runLater(() -> {
            stage.getScene().setRoot(new LoginView().getRoot());
            stage.setWidth(1000);
            stage.setHeight(620);
            stage.centerOnScreen();
        });
    }

    public static void main(String[] args) { launch(args); }
}
