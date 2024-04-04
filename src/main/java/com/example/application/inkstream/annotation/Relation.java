package com.example.application.inkstream.annotation;

import java.util.*;
import java.util.stream.Stream;

public class Relation<V> {

    private List<V> records;

    public Relation() {
        this.records = new ArrayList<>();
    }

    public Relation(List<V> records) {
        this.records = records;
    }

    public Stream<V> getRecords() {
        return records.stream();
    }

    public int size() {
        return records.size();
    }

    public void add(V e) {
        records.add(e);
    }
}
