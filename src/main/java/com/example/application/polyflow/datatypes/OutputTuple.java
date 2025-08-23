package com.example.application.polyflow.datatypes;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class OutputTuple extends AbstractEntity {


    public String getRecord_Id() {
        return "";
    }
    public void setRecord_Id(String record_Id) {}

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
