package com.example.application.inkstream.record;

import java.util.List;

public class SimpleEventFactory<V> implements EventBeanFactory<V>{
    @Override
    public EventBean<V> make(List<String> attributes, List<V> values, long timestamp) {
        return new SimpleEvent<>(values.get(0), timestamp);
    }
}
