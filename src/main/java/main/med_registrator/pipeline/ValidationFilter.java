package main.med_registrator.pipeline;

import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;

public class ValidationFilter implements Filter {

    @Override
    public void process(Questionnaire questionnaire, Appeal appeal) {
        if (questionnaire.getSymptoms() == null || questionnaire.getSymptoms().isEmpty()) {
            appeal.setErrorMessage("Выберите хотя бы один симптом.");
            return;
        }
        if (questionnaire.getAge() < 0 || questionnaire.getAge() > 120) {
            appeal.setErrorMessage("Введите корректный возраст (0–120).");
            return;
        }
        System.out.println("[Фильтр 1] Валидация пройдена. Симптомов: "
                + questionnaire.getSymptoms().size() + ", возраст: " + questionnaire.getAge());
    }
}