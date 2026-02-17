package com.healthtracking.model;

import java.sql.Date;

public class HealthData {
    private int id;
    private int userId;
    private Date date;
    private double weight;
    private double height;
    private int bloodPressureSystolic;
    private int bloodPressureDiastolic;
    private int heartRate;
    private int steps;
    private double sleepHours;
    private String notes;

    public HealthData() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public int getBloodPressureSystolic() { return bloodPressureSystolic; }
    public void setBloodPressureSystolic(int bloodPressureSystolic) { this.bloodPressureSystolic = bloodPressureSystolic; }

    public int getBloodPressureDiastolic() { return bloodPressureDiastolic; }
    public void setBloodPressureDiastolic(int bloodPressureDiastolic) { this.bloodPressureDiastolic = bloodPressureDiastolic; }

    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }

    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}