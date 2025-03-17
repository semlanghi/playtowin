package com.example.application.polyflow.datatypes;

import java.util.List;

public interface EventBean<V> extends Tuple {
    public V getValue(String attributeName);
    public long getTime();
    public V getValue();
    public String getLabel();

    public EventBean<V> project(List<String> attributes);

}
