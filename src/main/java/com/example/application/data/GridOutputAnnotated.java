package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class GridOutputAnnotated extends AbstractEntity {

    private Long percA;
    private Long percB;
    private long timestamp;
    private String simplifiedPolynomial;
    private int degree;
    private int variablesCardinality;
    private String win;
    private String polynomial;


    public String getPolynomial() {
        return polynomial;
    }

    public void setPolynomial(String polynomial) {
        this.polynomial = polynomial;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getSimplifiedPolynomial() {
        return simplifiedPolynomial;
    }

    public void setSimplifiedPolynomial(String simplifiedPolynomial) {
        this.simplifiedPolynomial = simplifiedPolynomial;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getVariablesCardinality() {
        return variablesCardinality;
    }

    public void setVariablesCardinality(int variablesCardinality) {
        this.variablesCardinality = variablesCardinality;
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
