package com.example.application.data;

import com.example.application.inkstream.record.EventBean;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class GridInput extends AbstractEntity implements EventBean<Long> {

    private String Cursor;
    private Long ZoneACons;
    private Long ZoneBCons;
    private long timestamp;

    public String getCursor() {
        return Cursor;
    }

    public void setCursor(String cursor) {
        Cursor = cursor;
    }

    public Long getZoneACons() {
        return ZoneACons;
    }

    public void setZoneACons(Long zoneACons) {
        ZoneACons = zoneACons;
    }

    public Long getZoneBCons() {
        return ZoneBCons;
    }

    public void setZoneBCons(Long zoneBCons) {
        ZoneBCons = zoneBCons;
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
                return this.ZoneACons;
            case "consB":
                return this.ZoneBCons;
            default:
                return this.ZoneACons;
        }
    }

    @Override
    public long getTime() {
        return timestamp;
    }

    @Override
    public Long getValue() {
        return this.ZoneACons;
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
                ", ZoneACons=" + ZoneACons +
                ", ZoneBCons=" + ZoneBCons +
                ", timestamp=" + timestamp +
                '}';
    }
}
