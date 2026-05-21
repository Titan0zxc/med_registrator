package main.med_registrator.pipeline;

import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;
import main.med_registrator.model.Symptom;

public class ClassificationFilter implements Filter {

    @Override
    public void process(Questionnaire questionnaire, Appeal appeal) {
        if (appeal.hasError()) return;

        int total = 0;
        boolean hasCritical = false;

        for (Symptom s : questionnaire.getSymptoms()) {
            total += s.getScore();
            if (s.getScore() == 5) hasCritical = true;
        }

        questionnaire.setTotalScore(total);

        Appeal.Priority priority;
        if (hasCritical || total >= 5) {
            priority = Appeal.Priority.RED;
        } else if (total >= 3) {
            priority = Appeal.Priority.YELLOW;
        } else {
            priority = Appeal.Priority.GREEN;
        }

        appeal.setPriority(priority);
        System.out.println("[Фильтр 2] Балл: " + total + ", приоритет: " + priority);
    }
}