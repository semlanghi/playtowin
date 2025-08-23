package com.example.application.data;

import com.example.application.polyflow.datatypes.EventBean;
import com.example.application.polyflow.datatypes.Tuple;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class GridInput extends AbstractEntity implements EventBean<Long> {

    private String Cursor;
    private String record_Id;
    private Long consA;
    private Long consB;
    private long timestamp;


    public String getRecord_Id() {
        return record_Id;
    }

    public void setRecord_Id(String record_Id) {
        this.record_Id = record_Id;
    }

    public String getCursor() {
        return Cursor;
    }

    public void setCursor(String cursor) {
        Cursor = cursor;
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

    @Override
    public Long getValue(String attributeName) {
        switch (attributeName){
            case "consA":
                return this.consA;
            case "consB":
                return this.consB;
            default:
                return this.consA;
        }
    }

    @Override
    public long getTime() {
        return timestamp;
    }

    @Override
    public Long getValue() {
        return this.consA;
    }

    @Override
    public String getLabel() {
        return "electric";
    }

    @Override
    public EventBean<Long> project(List<String> attributes) {
        return null;
    }

    @Override
    public String toString() {
        return "GridInput{" +
                "Cursor='" + Cursor + '\'' +
                ", ZoneACons=" + consA +
                ", ZoneBCons=" + consB +
                ", timestamp=" + timestamp +
                '}';
    }

}
