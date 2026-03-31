module main.med_registrator {
    requires javafx.controls;
    requires javafx.fxml;


    opens main.med_registrator to javafx.fxml;
    exports main.med_registrator;
}