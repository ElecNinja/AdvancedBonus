module adv.bonus.advanced {
    requires javafx.controls;
    requires javafx.fxml;


    opens adv.bonus.advanced to javafx.fxml;
    exports adv.bonus.advanced;
}