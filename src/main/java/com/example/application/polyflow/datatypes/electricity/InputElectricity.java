package com.example.application.polyflow.datatypes.electricity;

import com.example.application.polyflow.datatypes.Tuple;
import jakarta.persistence.Entity;

@Entity
public class InputElectricity extends Tuple {

    private String record_Id;
    private double cons_A;
    private double cons_B;
    private long timestamp;
    private String operatorId;
    private String intervalId;
    private String cursor;
    private String attributeForComputation;

    @Override
    public Tuple copy() {
        InputElectricity copy = new InputElectricity();
        copy.setCursor(this.cursor);
        copy.setTimestamp(this.timestamp);
        copy.setOperatorId(this.operatorId);
        copy.setIntervalId(this.intervalId);
        copy.setRecord_Id(this.record_Id);
        copy.setCons_B(this.cons_B);
        copy.setCons_A(this.cons_A);
        copy.setAttributeForComputation(this.attributeForComputation);
        return copy;
    }

    public String getRecord_Id() {
        return record_Id;
    }

    public void setRecord_Id(String record_Id) {
        this.record_Id = record_Id;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getIntervalId() {
        return intervalId;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public void setIntervalId(String intervalId) {
        this.intervalId = intervalId;
    }

    public double getCons_A() {
        return cons_A;
    }

    public void setCons_A(double consA) {
        this.cons_A = consA;
    }

    public double getCons_B() {
        return cons_B;
    }

    public void setCons_B(double consB) {
        this.cons_B = consB;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }

    @Override
    public void setAttributeForComputation(String attributeForComputation) {
        this.attributeForComputation = attributeForComputation;
    }

}
