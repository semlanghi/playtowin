package com.example.application.polyflow.datatypes.nyctaxi;

import com.example.application.polyflow.datatypes.Tuple;
import jakarta.persistence.Entity;

import java.lang.reflect.Field;

@Entity
public class InputTaxi extends Tuple {

    private String medallion;
    private String hack_license;
    private long pickup_datetime;
    private long dropoff_datetime;
    private double trip_time_in_secs;
    private double trip_distance;
    private double pickup_longitude;
    private double pickup_latitude;
    private double dropoff_longitude;
    private double dropoff_latitude;
    private String payment_type;
    private double fare_amount;
    private double surcharge;
    private double mta_tax;
    private double tip_amount;
    private double tolls_amount;
    private double total_amount;
    private String recordId;
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
        copy.setRecordId(this.recordId);
        copy.setMedallion(this.medallion);
        copy.setHack_license(this.hack_license);
        copy.setPickup_datetime(this.pickup_datetime);
        copy.setDropoff_datetime(this.dropoff_datetime);
        copy.setTrip_time_in_secs(this.trip_time_in_secs);
        copy.setTrip_distance(this.trip_distance);
        copy.setPickup_longitude(this.pickup_longitude);
        copy.setPickup_latitude(this.pickup_latitude);
        copy.setDropoff_longitude(this.dropoff_longitude);
        copy.setDropoff_latitude(this.dropoff_latitude);
        copy.setPayment_type(this.payment_type);
        copy.setFare_amount(this.fare_amount);
        copy.setSurcharge(this.surcharge);
        copy.setMta_tax(this.mta_tax);
        copy.setTip_amount(this.tip_amount);
        copy.setTolls_amount(this.tolls_amount);
        copy.setTotal_amount(this.total_amount);
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

    public String getMedallion() {
        return medallion;
    }

    public void setMedallion(String medallion) {
        this.medallion = medallion;
    }

    public String getHack_license() {
        return hack_license;
    }

    public void setHack_license(String hack_license) {
        this.hack_license = hack_license;
    }

    public long getPickup_datetime() {
        return pickup_datetime;
    }

    public void setPickup_datetime(long pickup_datetime) {
        this.pickup_datetime = pickup_datetime;
    }

    public long getDropoff_datetime() {
        return dropoff_datetime;
    }

    public void setDropoff_datetime(long dropoff_datetime) {
        this.dropoff_datetime = dropoff_datetime;
    }

    public double getTrip_time_in_secs() {
        return trip_time_in_secs;
    }

    public void setTrip_time_in_secs(double trip_time_in_secs) {
        this.trip_time_in_secs = trip_time_in_secs;
    }

    public double getTrip_distance() {
        return trip_distance;
    }

    public void setTrip_distance(double trip_distance) {
        this.trip_distance = trip_distance;
    }

    public double getPickup_longitude() {
        return pickup_longitude;
    }

    public void setPickup_longitude(double pickup_longitude) {
        this.pickup_longitude = pickup_longitude;
    }

    public double getPickup_latitude() {
        return pickup_latitude;
    }

    public void setPickup_latitude(double pickup_latitude) {
        this.pickup_latitude = pickup_latitude;
    }

    public double getDropoff_longitude() {
        return dropoff_longitude;
    }

    public void setDropoff_longitude(double dropoff_longitude) {
        this.dropoff_longitude = dropoff_longitude;
    }

    public double getDropoff_latitude() {
        return dropoff_latitude;
    }

    public void setDropoff_latitude(double dropoff_latitude) {
        this.dropoff_latitude = dropoff_latitude;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public double getFare_amount() {
        return fare_amount;
    }

    public void setFare_amount(double fare_amount) {
        this.fare_amount = fare_amount;
    }

    public double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(double surcharge) {
        this.surcharge = surcharge;
    }

    public double getMta_tax() {
        return mta_tax;
    }

    public void setMta_tax(double mta_tax) {
        this.mta_tax = mta_tax;
    }

    public double getTip_amount() {
        return tip_amount;
    }

    public void setTip_amount(double tip_amount) {
        this.tip_amount = tip_amount;
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
