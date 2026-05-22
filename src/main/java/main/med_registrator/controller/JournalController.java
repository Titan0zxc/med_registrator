package main.med_registrator.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.med_registrator.db.DatabaseManager;

import java.sql.SQLException;
import java.util.List;

public class JournalController {

    @FXML private TableView<String[]> table;
    @FXML private TableColumn<String[], String> colId;
    @FXML private TableColumn<String[], String> colName;
    @FXML private TableColumn<String[], String> colAge;
    @FXML private TableColumn<String[], String> colPriority;
    @FXML private TableColumn<String[], String> colDate;
    @FXML private TableColumn<String[], String> colType;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colAge.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colPriority.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        colDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));
        colType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[5]));

        // Цвет строк по приоритету
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    setStyle(switch (item[3]) {
                        case "RED"    -> "-fx-background-color: #FFEBEE;";
                        case "YELLOW" -> "-fx-background-color: #FFFDE7;";
                        case "GREEN"  -> "-fx-background-color: #E8F5E9;";
                        default -> "";
                    });
                }
            }
        });

        loadData();
    }

    private void loadData() {
        try {
            List<String[]> data = DatabaseManager.getInstance().getAllAppeals();
            table.setItems(FXCollections.observableArrayList(data));
            statusLabel.setText("Записей в журнале: " + data.size());
        } catch (SQLException e) {
            statusLabel.setText("Ошибка загрузки: " + e.getMessage());
        }
    }

    @FXML
    private void onRefresh() { loadData(); }

    @FXML
    private void onClose() {
        ((Stage) table.getScene().getWindow()).close();
    }
}