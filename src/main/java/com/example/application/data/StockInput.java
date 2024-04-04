package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class StockInput extends AbstractEntity {

    private String Cursor;
    private String name;
    private Integer dollars;
    private long timestamp;

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
