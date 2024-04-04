package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class StockOutputQuantified extends AbstractEntity {

    private String name;
    private Float avg;
    private long timestamp;
    private String simplifiedPolynomial;
    private int degree;
    private int variablesCardinality;

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



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getAvg() {
        return avg;
    }

    public void setAvg(Float avg) {
        this.avg = avg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }

    public String getSimplifiedPolynomial() {
        return simplifiedPolynomial;
    }

    public void setSimplifiedPolynomial(String polynomial) {
        this.simplifiedPolynomial = polynomial;
    }
}
