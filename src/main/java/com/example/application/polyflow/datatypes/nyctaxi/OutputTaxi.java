package com.example.application.polyflow.datatypes.nyctaxi;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class OutputTaxi extends AbstractEntity {
    private String recordId;
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


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }
}

