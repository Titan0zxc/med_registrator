package main.med_registrator.model;

public class Symptom {
    private final String name;
    private final int score;

    public Symptom(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() { return name; }
    public int getScore() { return score; }

    @Override
    public String toString() { return name + " (" + score + " пт)"; }
}