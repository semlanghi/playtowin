package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class GridOutputQuantified extends AbstractEntity {

    private Long percA;
    private Long percB;
    private long timestamp;
    private String polynomial;

    public String getPolynomial() {
        return polynomial;
    }

    public void setPolynomial(String polynomial) {
        this.polynomial = polynomial;
    }

    public Long getPercA() {
        return percA;
    }

    public void setPercA(Long percA) {
        this.percA = percA;
    }

    public Long getPercB() {
        return percB;
    }

    public void setPercB(Long percB) {
        this.percB = percB;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }
}
