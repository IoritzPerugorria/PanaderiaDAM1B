module org.example.panaderiadam1b {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.panaderiadam1b to javafx.fxml;
    exports org.example.panaderiadam1b;
}