package main.med_registrator.handler;

import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;
import main.med_registrator.model.Ticket;

public class YellowHandler {
    public Ticket handle(Questionnaire q, Appeal appeal) {
        // Заглушка: имитирует ответ сервиса вызова врача на дом
        int queuePos = (int)(Math.random() * 5 + 1);
        String details = """
            🟡 ВЫЗОВ ВРАЧА НА ДОМ
            Ваш номер в очереди: %d
            Ожидаемое время визита: в течение 2–4 часов
            
            Врач свяжется с вами по телефону перед визитом.
            Не принимайте пищу, оставайтесь дома.
            """.formatted(queuePos);

        return new Ticket("YELLOW", "Вызов врача на дом", details);
    }
}