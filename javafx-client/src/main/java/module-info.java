module com.printdock.client {
    requires javafx.controls;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    opens com.printdock.client to javafx.graphics;
}
