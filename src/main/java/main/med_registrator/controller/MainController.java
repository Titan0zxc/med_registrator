package main.med_registrator.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.med_registrator.MedApplication;
import main.med_registrator.config.ScaleConfig;
import main.med_registrator.model.Questionnaire;
import main.med_registrator.model.Symptom;
import main.med_registrator.model.Ticket;
import main.med_registrator.pipeline.Pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private TextField fullNameField;   // НОВОЕ
    @FXML private Spinner<Integer> ageSpinner;
    @FXML private TextField chronicField;
    @FXML private VBox criticalBox;
    @FXML private VBox acuteBox;
    @FXML private VBox mildBox;
    @FXML private Label statusLabel;
    @FXML private TextField policyField;

    private final List<CheckBox> symptomCheckboxes = new ArrayList<>();

    @FXML
    public void initialize() {
        ageSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 30)
        );
        addSymptoms(criticalBox, ScaleConfig.CRITICAL, "🔴 ");
        addSymptoms(acuteBox,    ScaleConfig.ACUTE,    "🟡 ");
        addSymptoms(mildBox,     ScaleConfig.MILD,      "🟢 ");

        policyField.setTextFormatter(new javafx.util.converter.DefaultStringConverter() != null ?
                new javafx.scene.control.TextFormatter<>(change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("\\d{0,16}")) return change;
                    return null;
                }) : null);
        policyField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,16}")) return change;
            return null;
        }));
    }

    private void addSymptoms(VBox box, List<Symptom> symptoms, String prefix) {
        for (Symptom s : symptoms) {
            CheckBox cb = new CheckBox(prefix + s.getName() + "  [" + s.getScore() + " пт]");
            cb.setUserData(s);
            cb.setWrapText(true);
            symptomCheckboxes.add(cb);
            box.getChildren().add(cb);
        }
    }

    @FXML
    private void onSubmit() {
        String fullName = fullNameField.getText().trim();
        String policy = policyField.getText().trim();
        if (policy.isEmpty()) {
            statusLabel.setText("⚠ Введите номер полиса ОМС (16 цифр).");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        if (policy.length() != 16) {
            statusLabel.setText("⚠ Номер полиса должен содержать ровно 16 цифр.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (fullName.isEmpty()) {
            statusLabel.setText("⚠ Введите ФИО пациента.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        List<Symptom> selected = symptomCheckboxes.stream()
                .filter(CheckBox::isSelected)
                .map(cb -> (Symptom) cb.getUserData())
                .toList();

        int age = ageSpinner.getValue();
        String chronic = chronicField.getText().trim();

        Questionnaire q = new Questionnaire(fullName, policy, age, chronic, selected);
        Pipeline pipeline = new Pipeline();
        Pipeline.Result result = pipeline.run(q);

        if (!result.success()) {
            statusLabel.setText("⚠ " + result.errorMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        statusLabel.setText("✓ Талон сформирован для: " + fullName);
        statusLabel.setStyle("-fx-text-fill: green;");

        try {
            showTicket(result.ticket());
        } catch (IOException e) {
            statusLabel.setText("Ошибка открытия талона: " + e.getMessage());
        }

    }

    @FXML
    private void onClear() {
        symptomCheckboxes.forEach(cb -> cb.setSelected(false));
        fullNameField.clear();
        policyField.clear();
        chronicField.clear();
        ageSpinner.getValueFactory().setValue(30);
        statusLabel.setText("");
        statusLabel.setStyle("");
    }

    @FXML
    private void onOpenJournal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MedApplication.class.getResource("journal-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 1200, 1000);
            Stage stage = new Stage();
            stage.setTitle("Журнал обращений");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Ошибка открытия журнала: " + e.getMessage());
        }
    }

    private void showTicket(Ticket ticket) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MedApplication.class.getResource("ticket-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 500, 500);
        TicketController controller = loader.getController();
        controller.setTicket(ticket);

        Stage stage = new Stage();
        stage.setTitle("Ваш талон");
        stage.setScene(scene);
        stage.show();
    }
}