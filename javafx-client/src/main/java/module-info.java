module com.printdock.client {
    requires javafx.controls;
    requires javafx.swing;
    requires java.net.http;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    opens com.printdock.client to javafx.graphics;
}
