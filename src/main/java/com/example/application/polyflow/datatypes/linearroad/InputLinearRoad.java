package com.example.application.polyflow.datatypes.linearroad;

import com.example.application.polyflow.datatypes.Tuple;
import jakarta.persistence.Entity;

import java.lang.reflect.Field;

@Entity
public class InputLinearRoad extends Tuple {

    private String recordId;

    private int car_id;
    private double speed;
    private int exp_way;
    private int lane;
    private int direction;
    private double x_pos;
    private long timestamp;
    private String operatorId;
    private String intervalId;
    private String cursor;
    private String attributeForComputation;



    @Override
    public Tuple copy() {
        InputLinearRoad copy = new InputLinearRoad();
        copy.setCursor(this.cursor);
        copy.setTimestamp(this.timestamp);
        copy.setOperatorId(this.operatorId);
        copy.setIntervalId(this.intervalId);
        copy.setRecordId(this.recordId);
        copy.setCar_id(this.car_id);
        copy.setSpeed(this.speed);
        copy.setExp_way(this.exp_way);
        copy.setLane(this.lane);
        copy.setDirection(this.direction);
        copy.setX_pos(this.x_pos);
        copy.setAttributeForComputation(this.attributeForComputation);
        return copy;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getExp_way() {
        return exp_way;
    }

    public void setExp_way(int exp_way) {
        this.exp_way = exp_way;
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getX_pos() {
        return x_pos;
    }

    public void setX_pos(double x_pos) {
        this.x_pos = x_pos;
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
