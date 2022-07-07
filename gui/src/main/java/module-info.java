module com.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.jetbrains.annotations;


    opens com.example.gui to javafx.fxml;
    exports com.example.gui;
}