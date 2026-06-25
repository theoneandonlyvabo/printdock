package com.printdock.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        LoginView login = new LoginView();
        Scene scene = new Scene(login.getRoot(), 1000, 620);
        stage.setTitle("PrintDock - Login");
        stage.setScene(scene);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
