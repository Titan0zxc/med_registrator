package main.med_registrator.handler;

import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;
import main.med_registrator.model.Ticket;

public class RedHandler {
    public Ticket handle(Questionnaire q, Appeal appeal) {
        // Заглушка: имитирует ответ службы скорой
        String eta = "~15 минут";
        String details = """
            🚨 ВЫЗОВ СКОРОЙ ПОМОЩИ
            Номер вызова: %s
            Время запроса: сейчас
            Ожидаемое время прибытия: %s
            
            Оставайтесь на месте. Откройте дверь.
            При ухудшении состояния звоните: 103
            """.formatted(generateCallNumber(), eta);

        return new Ticket("RED", "Вызов скорой помощи", details);
    }

    private String generateCallNumber() {
        return "EM-" + (int)(Math.random() * 90000 + 10000);
    }
}