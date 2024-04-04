package com.example.application.inkstream.constraint;

import com.example.application.inkstream.record.EventBean;

import java.util.Objects;

public abstract class SpeedConstraint<V> implements StreamingConstraint<EventBean<V>> {

    private EventBean<V> origin;
    protected String description;

    public SpeedConstraint(EventBean<V> origin) {
        this.origin = origin;
        this.description = "SC";
    }

    public SpeedConstraint(EventBean<V> origin, String description) {
        this.origin = origin;
        this.description = description;
    }

    @Override
    public abstract long checkConstraint(EventBean<V> value);

    public EventBean<V> getOrigin() {
        return origin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpeedConstraint)) return false;
        SpeedConstraint<?> that = (SpeedConstraint<?>) o;
        return Objects.equals(origin, that.origin);
    }

    @Override
    public String getDescription() {
        return description + "_"+origin.getTime();
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin);
    }
}
