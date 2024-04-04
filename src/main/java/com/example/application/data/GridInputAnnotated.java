package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class GridInputAnnotated extends AbstractEntity {

    private Long ZoneACons;
    private Long ZoneBCons;
    private long timestamp;
    private String polynomial;

    public String getPolynomial() {
        return polynomial;
    }

    public void setPolynomial(String polynomial) {
        this.polynomial = polynomial;
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
}
