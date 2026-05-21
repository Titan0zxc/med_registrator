package main.med_registrator.model;

import java.util.List;

public class Questionnaire {
    private String fullName;  
    private int age;
    private String chronic;
    private List<Symptom> symptoms;
    private int totalScore;

    public Questionnaire(String fullName, int age, String chronic, List<Symptom> symptoms) {
        this.fullName = fullName;
        this.age = age;
        this.chronic = chronic;
        this.symptoms = symptoms;
    }

    public String getFullName() { return fullName; }
    public int getAge() { return age; }
    public String getChronic() { return chronic; }
    public List<Symptom> getSymptoms() { return symptoms; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
}