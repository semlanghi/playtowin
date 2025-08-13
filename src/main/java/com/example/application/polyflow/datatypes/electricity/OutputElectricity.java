package com.example.application.polyflow.datatypes.electricity;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class OutputElectricity extends AbstractEntity {

    private String recordId;
    private Long consA;
    private Long consB;
    private long timestamp;
    public String operatorId;
    public String intervalId;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
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
