module main.med_registrator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens main.med_registrator to javafx.fxml;
    opens main.med_registrator.controller to javafx.fxml;
    exports main.med_registrator;
}