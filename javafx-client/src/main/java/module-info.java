module com.printdock.client {
    requires javafx.controls;
    requires java.net.http;
    opens com.printdock.client to javafx.graphics;
}
