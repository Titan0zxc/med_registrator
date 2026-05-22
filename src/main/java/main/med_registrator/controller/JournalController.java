package main.med_registrator.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.med_registrator.db.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class JournalController {

    // Журнал
    @FXML private TableView<String[]> table;
    @FXML private TableColumn<String[], String> colId;
    @FXML private TableColumn<String[], String> colName;
    @FXML private TableColumn<String[], String> colAge;
    @FXML private TableColumn<String[], String> colPriority;
    @FXML private TableColumn<String[], String> colDate;
    @FXML private TableColumn<String[], String> colType;
    @FXML private Label statusLabel;

    // Статистика
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private Label lblTotal;
    @FXML private Label lblRed;
    @FXML private Label lblYellow;
    @FXML private Label lblGreen;
    @FXML private VBox barRed;
    @FXML private VBox barYellow;
    @FXML private VBox barGreen;
    @FXML private TableView<String[]> statTable;
    @FXML private TableColumn<String[], String> stColId;
    @FXML private TableColumn<String[], String> stColName;
    @FXML private TableColumn<String[], String> stColAge;
    @FXML private TableColumn<String[], String> stColPriority;
    @FXML private TableColumn<String[], String> stColDate;
    @FXML private TableColumn<String[], String> stColType;
    @FXML private Label statStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Колонки журнала
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colAge.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colPriority.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        colDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));
        colType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[5]));
        setupRowColors(table);

        // Колонки статистики
        stColId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        stColName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        stColAge.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        stColPriority.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        stColDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));
        stColType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[5]));
        setupRowColors(statTable);

        // Даты по умолчанию: последние 7 дней
        dateTo.setValue(LocalDate.now());
        dateFrom.setValue(LocalDate.now().minusDays(7));

        loadData();
        loadStats();
    }

    private void setupRowColors(TableView<String[]> tv) {
        tv.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                setStyle(switch (item[3]) {
                    case "RED"    -> "-fx-background-color: #FFEBEE;";
                    case "YELLOW" -> "-fx-background-color: #FFFDE7;";
                    case "GREEN"  -> "-fx-background-color: #E8F5E9;";
                    default -> "";
                });
            }
        });
    }

    private void loadData() {
        try {
            List<String[]> data = DatabaseManager.getInstance().getAllAppeals();
            table.setItems(FXCollections.observableArrayList(data));
            statusLabel.setText("Записей в журнале: " + data.size());
        } catch (SQLException e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void onLoadStats() {
        loadStats();
    }

    private void loadStats() {
        if (dateFrom.getValue() == null || dateTo.getValue() == null) return;

        String from = dateFrom.getValue().format(FMT);
        String to   = dateTo.getValue().format(FMT);

        try {
            // Статистика по приоритетам
            Map<String, Integer> stats = DatabaseManager.getInstance().getStatsByPeriod(from, to);
            int red    = stats.get("RED");
            int yellow = stats.get("YELLOW");
            int green  = stats.get("GREEN");
            int total  = red + yellow + green;

            lblTotal.setText("Всего обращений: " + total);

            double redPct    = total > 0 ? (red    * 100.0 / total) : 0;
            double yellowPct = total > 0 ? (yellow * 100.0 / total) : 0;
            double greenPct  = total > 0 ? (green  * 100.0 / total) : 0;

            lblRed.setText(String.format("🔴 Скорая: %d (%.1f%%)", red, redPct));
            lblYellow.setText(String.format("🟡 Врач на дом: %d (%.1f%%)", yellow, yellowPct));
            lblGreen.setText(String.format("🟢 Поликлиника: %d (%.1f%%)", green, greenPct));

            // Цветные полосы-бары (высота пропорциональна проценту, макс 150px)
            barRed.setPrefHeight(total > 0 ? Math.max(4, redPct * 1.5) : 4);
            barYellow.setPrefHeight(total > 0 ? Math.max(4, yellowPct * 1.5) : 4);
            barGreen.setPrefHeight(total > 0 ? Math.max(4, greenPct * 1.5) : 4);

            // Таблица обращений за период
            List<String[]> rows = DatabaseManager.getInstance().getAppealsByPeriod(from, to);
            statTable.setItems(FXCollections.observableArrayList(rows));
            statStatus.setText(String.format("Период: %s — %s, записей: %d", from, to, rows.size()));

        } catch (SQLException e) {
            statStatus.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML private void onRefresh() { loadData(); }
    @FXML private void onRefreshStats() { loadStats(); }

    @FXML
    private void onPeriodDay()   { dateFrom.setValue(LocalDate.now()); dateTo.setValue(LocalDate.now()); loadStats(); }
    @FXML
    private void onPeriodWeek()  { dateFrom.setValue(LocalDate.now().minusDays(6)); dateTo.setValue(LocalDate.now()); loadStats(); }
    @FXML
    private void onPeriodMonth() { dateFrom.setValue(LocalDate.now().minusDays(29)); dateTo.setValue(LocalDate.now()); loadStats(); }
    @FXML
    private void onPeriodAll()   { dateFrom.setValue(LocalDate.of(2020,1,1)); dateTo.setValue(LocalDate.now()); loadStats(); }

    @FXML
    private void onClose() { ((Stage) table.getScene().getWindow()).close(); }
}