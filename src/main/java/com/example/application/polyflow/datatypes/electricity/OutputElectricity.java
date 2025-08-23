package com.example.application.polyflow.datatypes.electricity;

import com.example.application.data.AbstractEntity;
import com.example.application.polyflow.datatypes.OutputTuple;
import com.example.application.polyflow.datatypes.Tuple;
import jakarta.persistence.Entity;

@Entity
public class OutputElectricity extends OutputTuple {

    private String record_Id;
    private Long consA;
    private Long consB;
    private long timestamp;
    public String operatorId;
    public String intervalId;

    public String getRecord_Id() {
        return record_Id;
    }

    public void setRecord_Id(String record_Id) {
        this.record_Id = record_Id;
    }

    public String getOperatorId() {
        return operatorId;
    }
    public String getIntervalId(){return intervalId;}

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
    public void setIntervalId(String intervalId) {
        this.intervalId = intervalId;
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
