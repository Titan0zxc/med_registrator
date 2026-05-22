package main.med_registrator.model;

import java.util.List;

public class Questionnaire {
    private String fullName;
    private String insurancePolicy;
    private int age;
    private String chronic;
    private List<Symptom> symptoms;
    private int totalScore;

    public Questionnaire(String fullName, String insurancePolicy,
                         int age, String chronic, List<Symptom> symptoms) {
        this.fullName = fullName;
        this.insurancePolicy = insurancePolicy;
        this.age = age;
        this.chronic = chronic;
        this.symptoms = symptoms;
    }

    public String getFullName() { return fullName; }
    public String getInsurancePolicy() { return insurancePolicy; }
    public int getAge() { return age; }
    public String getChronic() { return chronic; }
    public List<Symptom> getSymptoms() { return symptoms; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
}