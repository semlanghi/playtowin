package com.example.application.inkstream.record;

import java.util.List;
import java.util.Objects;

public class SimpleEvent <V> implements EventBean<V> {
    private V value;
    private long timestamp;

    public SimpleEvent(V value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public V getValue(String attributeName) {
        return value;
    }

    @Override
    public long getTime() {
        return timestamp;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return "simple";
    }

    @Override
    public EventBean<V> project(List<String> attributes) {
        return this;
    }

    @Override
    public String toString() {
        return "record.SimpleEvent{" +
                "value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleEvent)) return false;
        SimpleEvent<?> that = (SimpleEvent<?>) o;
        return timestamp == that.timestamp && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, timestamp);
    }
}
