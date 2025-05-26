module com.example.testadvancedbonus {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;


    opens com.example.testadvancedbonus to javafx.fxml;
    exports com.example.testadvancedbonus;
}