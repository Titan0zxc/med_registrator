package main.med_registrator.pipeline;

import main.med_registrator.db.DatabaseManager;
import main.med_registrator.handler.GreenHandler;
import main.med_registrator.handler.RedHandler;
import main.med_registrator.handler.YellowHandler;
import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;
import main.med_registrator.model.Ticket;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class RoutingFilter implements Filter {

    private Ticket resultTicket;

    @Override
    public void process(Questionnaire questionnaire, Appeal appeal) {
        if (appeal.hasError()) return;

        try {
            String symptomsStr = questionnaire.getSymptoms().stream()
                    .map(s -> s.getName() + ":" + s.getScore())
                    .collect(Collectors.joining(", "));

            int qId = DatabaseManager.getInstance().saveQuestionnaire(
                    questionnaire.getFullName(),   // ФИО
                    symptomsStr,
                    questionnaire.getTotalScore(),
                    questionnaire.getChronic(),
                    questionnaire.getAge()
            );
            appeal.setQuestionnaireId(qId);

            Ticket ticket = switch (appeal.getPriority()) {
                case RED    -> new RedHandler().handle(questionnaire, appeal);
                case YELLOW -> new YellowHandler().handle(questionnaire, appeal);
                case GREEN  -> new GreenHandler().handle(questionnaire, appeal);
            };

            int appealId = DatabaseManager.getInstance().saveAppeal(qId, appeal.getPriority().name());
            DatabaseManager.getInstance().saveTicket(appealId, ticket.getType(), ticket.getDetails());
            ticket.setAppealId(appealId);
            this.resultTicket = ticket;

        } catch (SQLException e) {
            appeal.setErrorMessage("Ошибка БД: " + e.getMessage());
        }
    }

    public Ticket getResultTicket() { return resultTicket; }
}