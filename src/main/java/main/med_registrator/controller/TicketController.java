package main.med_registrator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.med_registrator.model.Ticket;

public class TicketController {

    @FXML private Label titleLabel;
    @FXML private TextArea detailsArea;
    @FXML private VBox root;

    public void setTicket(Ticket ticket) {
        titleLabel.setText(ticket.getTitle());
        detailsArea.setText(ticket.getDetails());

        // Цвет фона по приоритету
        String color = switch (ticket.getType()) {
            case "RED"    -> "#FFEBEE";
            case "YELLOW" -> "#FFFDE7";
            case "GREEN"  -> "#E8F5E9";
            default       -> "#FFFFFF";
        };
        root.setStyle("-fx-background-color: " + color + ";");
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}