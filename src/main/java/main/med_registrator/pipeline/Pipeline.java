package main.med_registrator.pipeline;

import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;
import main.med_registrator.model.Ticket;

public class Pipeline {

    private final ValidationFilter validation = new ValidationFilter();
    private final ClassificationFilter classification = new ClassificationFilter();
    private final RoutingFilter routing = new RoutingFilter();

    public Result run(Questionnaire questionnaire) {
        Appeal appeal = new Appeal();

        validation.process(questionnaire, appeal);
        classification.process(questionnaire, appeal);
        routing.process(questionnaire, appeal);

        if (appeal.hasError()) {
            return Result.error(appeal.getErrorMessage());
        }
        return Result.success(routing.getResultTicket());
    }

    public record Result(boolean success, Ticket ticket, String errorMessage) {
        public static Result success(Ticket t) { return new Result(true, t, null); }
        public static Result error(String msg) { return new Result(false, null, msg); }
    }
}