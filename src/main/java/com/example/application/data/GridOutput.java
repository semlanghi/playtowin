package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class GridOutput extends AbstractEntity {

    private String win;
    private Long percA;
    private Long percB;
    private long timestamp;

    public String getWin() {
        return win;
    }

    public void setWin(String window) {
        this.win = window;
    }

    public Long getPercA() {
        return percA;
    }

    public void setPercA(Long percA) {
        this.percA = percA;
    }

    public Long getPercB() {
        return percB;
    }

    public void setPercB(Long percB) {
        this.percB = percB;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }
}
