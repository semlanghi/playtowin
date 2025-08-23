package com.example.application.polyflow.datatypes.linearroad;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class OutputLinearRoad extends AbstractEntity {
    private String record_Id;
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


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }

}
