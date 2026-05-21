package main.med_registrator.model;

public class Ticket {
    private String type;       // RED / YELLOW / GREEN
    private String title;      // Заголовок талона
    private String details;    // Детали (врач, кабинет, ETA и т.д.)
    private int appealId;

    public Ticket(String type, String title, String details) {
        this.type = type;
        this.title = title;
        this.details = details;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public int getAppealId() { return appealId; }
    public void setAppealId(int appealId) { this.appealId = appealId; }
}