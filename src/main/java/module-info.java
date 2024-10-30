module com.example.service_squade {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires javafx.graphics;
    requires java.sql;

    opens com.example.service_squade to javafx.fxml;
    exports com.example.service_squade;
}