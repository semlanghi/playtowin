package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class GridInputAnnotated extends AbstractEntity {

    private String record_Id;
    private Long consA;
    private Long consB;
    private long timestamp;
    private String polynomial;

    public String getRecord_Id() {
        return record_Id;
    }

    public void setRecord_Id(String record_Id) {
        this.record_Id = record_Id;
    }

    public String getPolynomial() {
        return polynomial;
    }

    public void setPolynomial(String polynomial) {
        this.polynomial = polynomial;
    }

    public Long getConsA() {
        return consA;
    }

    public void setConsA(Long consA) {
        this.consA = consA;
    }

    public Long getConsB() {
        return consB;
    }

    public void setConsB(Long consB) {
        this.consB = consB;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }
}
