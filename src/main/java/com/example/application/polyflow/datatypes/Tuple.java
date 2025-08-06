package com.example.application.polyflow.datatypes;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class Tuple extends AbstractEntity {

    public Tuple copy(){return new Tuple();};
    public String getRecordId(){return "";};

    public void setRecordId(String recordId){};
    public String getOperatorId(){return "";};
    public String getIntervalId(){return "";};
    public String getCursor(){return "";};
    public void setCursor(String cursor){};
    public void setOperatorId(String operatorId){};
    public void setIntervalId(String intervalId){};
    public long getTimestamp(){return 0;};
    public void setTimestamp(long dateOfBirth){};
    public void setAttributeForComputation(String attributeForComputation){};
    public double getAttributeForComputation(String attributeForComputation){return 0;};
}
