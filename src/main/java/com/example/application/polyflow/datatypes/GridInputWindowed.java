package com.example.application.polyflow.datatypes;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@Entity
public class GridInputWindowed extends Tuple {

    private String recordId;
    private double consA;
    private double consB;
    private long timestamp;
    private String operatorId;
    private String intervalId;
    private String cursor;
    private String attributeForComputation;

    @Override
    public Tuple copy() {
        GridInputWindowed copy = new GridInputWindowed();
        copy.setCursor(this.cursor);
        copy.setTimestamp(this.timestamp);
        copy.setOperatorId(this.operatorId);
        copy.setIntervalId(this.intervalId);
        copy.setRecordId(this.recordId);
        copy.setConsB(this.consB);
        copy.setConsA(this.consA);
        copy.setAttributeForComputation(this.attributeForComputation);
        return copy;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
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

    public double getConsA() {
        return consA;
    }

    public void setConsA(double consA) {
        this.consA = consA;
    }

    public double getConsB() {
        return consB;
    }

    public void setConsB(double consB) {
        this.consB = consB;
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

    /*
        We use this method to dynamically get the Attribute that the user wants to compute over
        e.g. the user wants an Aggregate Frame and could want to Aggregate on the field ConsA or ConsB
    */
    @Override
    public double getAttributeForComputation(String attributeForComputation) {
        try {
            Field field = this.getClass().getDeclaredField(attributeForComputation);
            field.setAccessible(true); //it's private
            return field.getDouble(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Invalid attribute name: " + attributeForComputation, e);
        }
    }
}
