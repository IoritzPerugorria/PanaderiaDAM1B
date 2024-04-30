module org.example.panaderiadam1b {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens org.example.panaderiadam1b to javafx.fxml;
    exports org.example.panaderiadam1b;
    exports Controladores;
    opens Controladores to javafx.fxml;
}