package main.med_registrator.model;

public class Appeal {
    private int questionnaireId;
    private Priority priority;
    private String errorMessage;

    public enum Priority { RED, YELLOW, GREEN }

    public int getQuestionnaireId() { return questionnaireId; }
    public void setQuestionnaireId(int id) { this.questionnaireId = id; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public boolean hasError() { return errorMessage != null; }
}