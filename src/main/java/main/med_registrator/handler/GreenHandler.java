package main.med_registrator.handler;

import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;
import main.med_registrator.model.Ticket;

public class GreenHandler {

    private static final String[][] DOCTORS = {
            {"Иванова А.П.", "Терапевт", "205", "2"},
            {"Петров С.И.", "Терапевт", "207", "2"},
            {"Сидорова М.В.", "Терапевт", "310", "3"},
    };

    private static final String[] TIMES = {
            "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30"
    };

    public Ticket handle(Questionnaire q, Appeal appeal) {
        // Заглушка: выбираем случайного свободного врача и время
        String[] doctor = DOCTORS[(int)(Math.random() * DOCTORS.length)];
        String time = TIMES[(int)(Math.random() * TIMES.length)];

        String details = """
            🟢 ЗАПИСЬ В ПОЛИКЛИНИКУ
            Врач: %s (%s)
            Кабинет: %s, Этаж: %s
            Время приёма: сегодня в %s
            
            Возьмите с собой паспорт и полис ОМС.
            Приходите за 5 минут до назначенного времени.
            """.formatted(doctor[0], doctor[1], doctor[2], doctor[3], time);

        return new Ticket("GREEN", "Запись в поликлинику", details);
    }
}