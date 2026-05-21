package main.med_registrator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.med_registrator.model.Ticket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TicketController {

    @FXML private Label titleLabel;
    @FXML private TextArea detailsArea;
    @FXML private VBox root;
    @FXML private Label saveStatusLabel;

    private Ticket ticket;

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        titleLabel.setText(ticket.getTitle());
        detailsArea.setText(ticket.getDetails());

        String color = switch (ticket.getType()) {
            case "RED"    -> "#FFEBEE";
            case "YELLOW" -> "#FFFDE7";
            case "GREEN"  -> "#E8F5E9";
            default       -> "#FFFFFF";
        };
        root.setStyle("-fx-background-color: " + color + ";");
    }

    @FXML
    private void onSave() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить талон");
        chooser.setInitialFileName("talon.txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовый файл", "*.txt")
        );

        Stage stage = (Stage) root.getScene().getWindow();
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("=== ТАЛОН ПАЦИЕНТА ===\n");
                fw.write(ticket.getTitle() + "\n");
                fw.write("=".repeat(30) + "\n");
                fw.write(ticket.getDetails());
                saveStatusLabel.setText("✓ Сохранено: " + file.getName());
                saveStatusLabel.setStyle("-fx-text-fill: green;");
            } catch (IOException e) {
                saveStatusLabel.setText("Ошибка сохранения: " + e.getMessage());
                saveStatusLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}