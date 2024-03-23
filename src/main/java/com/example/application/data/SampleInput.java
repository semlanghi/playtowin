package com.example.application.data;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class SampleInput extends AbstractEntity {

    private Integer ZoneACons;
    private Integer ZoneBCons;
    private long timestamp;



    public Integer getZoneACons() {
        return ZoneACons;
    }

    public void setZoneACons(Integer zoneACons) {
        ZoneACons = zoneACons;
    }

    public Integer getZoneBCons() {
        return ZoneBCons;
    }

    public void setZoneBCons(Integer zoneBCons) {
        ZoneBCons = zoneBCons;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long dateOfBirth) {
        this.timestamp = dateOfBirth;
    }
}
