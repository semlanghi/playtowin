package com.example.application.polyflow.datatypes;

import com.example.application.polyflow.datatypes.EventBean;

import java.util.List;

public interface EventBeanFactory<V> {

    public EventBean<V> make(List<String> attributes, List<V> values, long timestamp);
}
