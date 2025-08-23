package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class StockInput extends AbstractEntity {

    private String Cursor;
    private String record_Id;
    private String name;
    private Integer dollars;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDollars() {
        return dollars;
    }

    public void setDollars(Integer dollars) {
        this.dollars = dollars;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }
}
