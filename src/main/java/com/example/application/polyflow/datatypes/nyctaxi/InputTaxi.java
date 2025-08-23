package com.example.application.polyflow.datatypes.nyctaxi;

import com.example.application.polyflow.datatypes.Tuple;
import jakarta.persistence.Entity;

import java.lang.reflect.Field;

@Entity
public class InputTaxi extends Tuple {

    private long pickup_datetime;
    private double trip_distance;
    private String payment_type;
    private double tolls_amount;
    private double total_amount;
    private String record_Id;
    private long timestamp;
    private String operatorId;
    private String intervalId;
    private String cursor;
    private String attributeForComputation;

    @Override
    public Tuple copy() {
        InputTaxi copy = new InputTaxi();
        copy.setCursor(this.cursor);
        copy.setTimestamp(this.timestamp);
        copy.setOperatorId(this.operatorId);
        copy.setIntervalId(this.intervalId);
        copy.setRecord_Id(this.record_Id);
        copy.setPickup_datetime(this.pickup_datetime);
        copy.setTrip_distance(this.trip_distance);
        copy.setPayment_type(this.payment_type);
        copy.setTolls_amount(this.tolls_amount);
        copy.setTotal_amount(this.total_amount);
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


    public long getPickup_datetime() {
        return pickup_datetime;
    }

    public void setPickup_datetime(long pickup_datetime) {
        this.pickup_datetime = pickup_datetime;
    }



    public double getTrip_distance() {
        return trip_distance;
    }

    public void setTrip_distance(double trip_distance) {
        this.trip_distance = trip_distance;
    }


    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }


    public double getTolls_amount() {
        return tolls_amount;
    }

    public void setTolls_amount(double tolls_amount) {
        this.tolls_amount = tolls_amount;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
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
