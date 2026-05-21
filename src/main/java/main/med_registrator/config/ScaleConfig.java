package main.med_registrator.config;

import main.med_registrator.model.Symptom;
import java.util.List;

public class ScaleConfig {

    // Критические симптомы (5 баллов)
    public static final List<Symptom> CRITICAL = List.of(
            new Symptom("Нарушение или потеря сознания", 5),
            new Symptom("Острая боль в груди / за грудиной", 5),
            new Symptom("Тяжёлое нарушение дыхания (удушье)", 5),
            new Symptom("Обильное фонтанирующее кровотечение", 5),
            new Symptom("Судороги, паралич лица или конечностей", 5)
    );

    // Острые симптомы (3 балла)
    public static final List<Symptom> ACUTE = List.of(
            new Symptom("Температура выше 38.5°C, не сбивается", 3),
            new Symptom("Выраженная острая боль (живот, спина, суставы)", 3),
            new Symptom("Резкий скачок давления с тошнотой / головокружением", 3),
            new Symptom("Многократная рвота или непрекращающаяся диарея", 3),
            new Symptom("Умеренное кровотечение из раны", 3)
    );

    // Лёгкие / плановые симптомы (1 балл)
    public static final List<Symptom> MILD = List.of(
            new Symptom("Субфебрильная температура (до 38.0°C)", 1),
            new Symptom("Незначительные боли (головная боль, дискомфорт)", 1),
            new Symptom("Насморк, кашель, першение в горле", 1),
            new Symptom("Продление рецепта / получение справки", 1),
            new Symptom("Хронические жалобы без ухудшения", 1)
    );

    public static List<Symptom> getAllSymptoms() {
        return java.util.stream.Stream.of(CRITICAL, ACUTE, MILD)
                .flatMap(List::stream)
                .toList();
    }
}