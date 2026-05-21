package main.med_registrator.pipeline;

import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;

public interface Filter {
    /**
     * Обрабатывает данные и передаёт результат дальше по цепочке.
     * Если фильтр обнаружил ошибку — устанавливает appeal.setErrorMessage().
     */
    void process(Questionnaire questionnaire, Appeal appeal);
}