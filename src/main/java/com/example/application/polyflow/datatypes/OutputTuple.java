package com.example.application.polyflow.datatypes;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class OutputTuple extends AbstractEntity {


    public String getRecordId() {
        return "";
    }
    public void setRecordId(String recordId) {}

    public String getOperatorId() {
        return "";
    }
    public String getIntervalId(){
        return "";
    }

    public void setOperatorId(String operatorId) {}
    public void setIntervalId(String intervalId) {}


    public long getTimestamp() {
        return 0;
    }

    public void setTimestamp(long dateOfBirth) {}
}
