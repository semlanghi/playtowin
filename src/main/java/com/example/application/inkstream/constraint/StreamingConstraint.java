package com.example.application.inkstream.constraint;

public interface StreamingConstraint<V> {
    long checkConstraint(V value);
    V getOrigin();

    String getDescription();
}
